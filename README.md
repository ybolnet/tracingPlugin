# tracingPlugin
Plugin allowing to add automatic logger (or any other process) to every call of methods you annotated with the annotation of your choice.

## setup
add to your project with this in toplevel build.gradle :

   ```
   buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath ("io.github.ybolnet:traceplugin:VERSION")
    }
} 
   ```



then to app-level build.gradle:


   ```
   plugins {
    ...
    id("io.github.ybolnet")
} 
   ```


So now, still in app build.gradle, you can add the necessary configurations, so that your app-level build gradle look like this:

```
plugins {
    ...
    id("io.github.ybolnet")
}

tracing{
    trackables = arrayOf("installDebug") // means that install debug will be traced
    config{
        //add a default config, that will trace every method annotated with annoation [@]DefTraceTest
        addDefaultConfig()
    }
}

android{
// usual android stuff
}

dependencies{
...
implementation("io.github.ybolnet:traceplugin:VERSION")
}
```

The plugin is a preprocessor. It parses the code, look for annotations, edit the annotatated methods to add the logs.
But since you only want to keep the code in this modified state for the build, the plugin is also able to remove all the added code and get back to the codebase original state.

Hence the plugins relies on three tasks, that are added after a sync (check in traceprogessing group)
- processTrace
- tracedInstallDebug
- unprocessTrace

processTrace will edit the code to add the logs.

unprocessTrace will get it back to what it was.

tracedInstallDebug will do : processTrace -> installDebug -> unprocessTrace. So can install a traced version of your app on the phone.
You can replace installDebug with any other task by adding them to "trackables" in the tracing closure. (see above)




## Restrictions:
The Trace annotations should work fine with methods in java and kotlin.
In kotlin the only restriction is when method are in the form
 ```
@MyAnnotation
fun foo(): Int = Something
 ```

This will not be picked up by the trace process task. Please use instead
 ```
@MyAnnotation
fun foo(): Int {
return something
}
 ```
Also, the plugin works best when annotation definitions are located alone in their own package.

## Example:

The SimplestTracer above :
```
class SimplestTracer : Tracer {
    override fun trace(
        defaultMessage: String,
        java: Boolean,
        method: Tracer.Method,
        history: LimitedSizeList<Tracer.Method>,
        parameterValues: Array<Any?>,
    ) {
        Log.d("TAG", defaultMessage)
    }
}
```

will result in this method:
```
@Trace
    fun test1(param: Int, param2: Int): Int {
        val v = 2
        return 1
    }
```

producing the log: 
``
your.package.com.something.test1(1 : java.lang.Integer, 2 : java.lang.Integer)
``

when called like this:
```
test1(1,2)
```


## Questions and clarifications

### Can I also trace the return values?

The plugin works by adding a line at the start of the traced method, and then removing this line when the build is over. So it allows you to have access to parameters and values, but not return value.
Yet there is still some way to add the returned value to the trace.

Have the following code somewhere:
```

package your.package.com.tracers

import your.package.com.annotations.YourAnnotation


fun <T> Any.withTrace(): T {
    val throwable = Throwable()
    val stackElement = throwable.stackTrace.run {
        if (size >= 2) {
            this[1]
        } else {
            null
        }
    }
    val fullMethodName = (stackElement?.className ?: "") + "." +  (stackElement?.methodName ?: "")
    return Returner().traceReturning(this as T, fullMethodName)
}

class Returner {
    @YourAnnotation
    fun <T> traceReturning(toTrace: T, callingMethod: String): T {
        return toTrace
    }
}

```

Then add withTrace() to any traced method whose return value you wan to trace:
```
    @YourAnnotation
    fun test1(): Int {
        ...
        return 1.withTrace()
    }
```

And finally plug in this tracer in your factory:
```
package your.package.com.tracers

import android.util.Log
import com.ybo.trackingplugin.tracerlib.LimitedSizeList
import com.ybo.trackingplugin.tracerlib.Tracer

class SpecialTracerWithReturns : Tracer {
    override fun trace(
        defaultMessage: String,
        java: Boolean,
        method: Tracer.Method,
        history: LimitedSizeList<Tracer.Method>,
        parameterValues: Array<Any?>,
    ) {
        if (method.possiblyObfuscatedMethod.startsWith(Returner().javaClass.name)) {
            if (parameterValues.size != 2) {
                throw Error("tracking error")
            }
            val returnedObject = parameterValues[0]
            val callingMethod =
                history.getMethodFromHistory(parameterValues[1] as String)?.originalName
                    ?: parameterValues[1] as String
            Log.d("TRACING_WITH_RETURNS", " $callingMethod returning $returnedObject")
        } else {
            Log.d("TRACING_WITH_RETURNS", defaultMessage)
        }
    }

    fun LimitedSizeList<Tracer.Method>.getMethodFromHistory(methodObf: String): Tracer.Method? {
        return this.find { it.possiblyObfuscatedMethod == methodObf }
    }
}
```
This would produce with the new tracer the following logs:

``

2024-02-04 12:10:33.942 27545-27545 TRACING_WITH_RETURNS    your.package.com           D   your.package.com.something.test1() 

2024-02-04 12:10:33.943 27545-27545 TRACING_WITH_RETURNS    your.package.com            D   your.package.com.something.test1 returning 1

``

