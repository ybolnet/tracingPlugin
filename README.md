# tracingPlugin
Plugin allowing to add automatic logger (or any other process) to every call of methods you annotated with the annotation of your choice.

add to your project with this in build.gradle toplevel:

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath ("io.github.ybolnet:traceplugin:VERSION")
    }
}


then to app-level build.gradle:


plugins {
    ...
    id("io.github.ybolnet")
}
now you must create in your code a set of 2 annotations that will represent the 2 states of the code: To-Be-Processed and Already-Processed.
For example :

annotation class SimpleTrace()
annotation class ReverseSimpleTrace()

The methods annotated with SimpleTrace will be the one producing the tracing logs. The annotation ReverseSimpleTrace will not be used by you but by the plugin, to mark the method it has already processed.

This plugin's job consists in adding a single line at the start of each method annotated with the annotation of your choice, and provides you a hook to be called each time the annotated method is called, with its arguments.

So now you have to implement your own tracer to be delegated when a traced method is called:

for example:

package your.package.com

import com.ybo.trackingplugin.tracerlib.Tracer


/** used in build.gradle */
class TracerFactory_Simple : Tracer.Factory {
    override fun create(): Tracer {
        return MyTracer() //<-- here put your implementation of Tracer class.
    }
}
