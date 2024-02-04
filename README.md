# tracingPlugin
Plugin allowing to add automatic logger (or any other process) to every call of methods you annotated with the annotation of your choice.

##setup
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


now you must create in your code a set of 2 annotations that will represent the 2 states of the code: To-Be-Processed and Already-Processed.
For example :

   ```
    annotation class SimpleTrace()
annotation class ReverseSimpleTrace()
   ```



The methods annotated with SimpleTrace will be the one producing the tracing logs. The annotation ReverseSimpleTrace will not be used by you but by the plugin, to mark the method it has already processed.

This plugin's job consists in adding a single line at the start of each method annotated with the annotation of your choice, and provides you with a hook to be called each time the annotated method is called, with its arguments.

So now you have to implement your own tracer to be delegated to when a traced method is called:

for example:

   ```
    package your.package.com

import com.ybo.trackingplugin.tracerlib.Tracer

import com.ybo.trackingplugin.tracerlib.Tracer

class TraceFactory : Tracer.Factory {
    override fun create(): Tracer {
        return MyTracerImpl() //<- here your implementation of tracer
    }
}
```
For instance the simplest tracer would be:
```
package your.package.com

import android.util.Log
import com.ybo.trackingplugin.tracerlib.LimitedSizeList
import com.ybo.trackingplugin.tracerlib.Tracer

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

So now in app build.gradle, you can plug in your tracer and your annotations by calling the "tracing" closure in app-level build.gradle.
It would make your build.gradle look like this:

```
plugins {
    ...
    id("io.github.ybolnet")
}

tracing{
    trackables = arrayOf("installDebug") // means that install debug will be traced
    config{
        //add a configuration for the couple @Trace/@UnTrace
        add { 
            name = "ANameForThisConfig"
            toBeProcessedAnnotation = "your.package.com.SimpleTrace"
            alreadyProcessedAnnotation = "your.package.com.ReverseSimpleTrace"
            srcPath = "../app/src/main" // where the source code is
            tracerFactory = "your.package.com.TraceFactory"
        }
        // and actually you can add as much configsthat you want.
    }
}

android{
...
}

dependencies{
...
implementation("io.github.ybolnet:traceplugin:VERSION")
}
```


don't forget to also add the lib packaged in the plugin still in app build.gradle:


```
    dependencies{
    implementation("io.github.ybolnet:traceplugin:VERSION")
    }
 ```
    
After sync, 3 tasks will be added in group traceprocessing:
- processTrace
- tracedInstallDebug
- unprocessTrace

processTrace will edit the code to add the logs.

unprocessTrace will get it back to what it was.

tracedInstallDebug will do : processTrace -> installDebug -> unprocessTrace. So can install a traced version of your app on the phone.
You can replace installDebug with any other task by adding them to "trackables" in the tracing closure.

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
    fun test1(): Int {
        val v = 2
        return 1
    }
```

producing the log: 

your.package.com.something.test1() 

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
2024-02-04 12:10:33.942 27545-27545 TRACING_WITH_RETURNS    com.ey.ybo.plugincustomer            D   com.ey.ybo.plugincustomer.MainActivity.test1() 
2024-02-04 12:10:33.943 27545-27545 TRACING_WITH_RETURNS    com.ey.ybo.plugincustomer            D   com.ey.ybo.plugincustomer.MainActivity.test1 returning 1
``

