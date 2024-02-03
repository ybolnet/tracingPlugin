# tracingPlugin
Plugin allowing to add automatic logger (or any other process) to every call of methods you annotated with the annotation of your choice.

add to your project with this in build. toplevel:

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

This plugin's job consists in adding a single line at the start of each method annotated with the annotation of your choice, and provides you a hook to be called each time the annotated method is called, with its arguments.

So now you have to implement your own tracer to be delegated when a traced method is called:

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


So now in app build.gradle, you can plug in your tracer and your annotations by writing:

```
    tracing{
    trackables = arrayOf("installDebug") // means that install debug will be traced
    config{
        //add a configuration for the couple @Trace/@UnTrace
        add { 
            name = "ANameForThisConfig"
            toBeProcessedAnnotation = "your.package.com.Trace"
            alreadyProcessedAnnotation = "your.package.com.UnTrace"
            srcPath = "../app/src/main" // where the source code is
            tracerFactory = "your.package.com.TraceFactory"
        }
        // and actually you can add as much configsthat you want.
    }
}
```


and eventually, still in app build.gradle:


```
    dependencies{
    implementation("io.github.ybolnet:traceplugin:0.0.17")
    }
 ```
    
After sync, 3 tasks will be added in group traceprocessing:
- processTrace
- tracedInstallDebug
- unprocessTrace

processTrace will edit the code to add the logs.
unprocessTrace will get it back to what it was.
tracedInstallDebug will do : processTrace -> installDebug -> unprocessTrace. So can install a traced version of your app on the phone.



