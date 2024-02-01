package com.ey.ybo.trackingplugin.annotations.tracers

import com.ey.ybo.trackinglib.tracer.Tracer


/** used in build.gradle */
class TracerFactory_Simple : Tracer.Factory {
    override fun create(): Tracer {
        println("Tracedo creating tracer")
        return TracerSimple()
    }
}
