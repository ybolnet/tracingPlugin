package com.ybo.trackingplugin.tracerlib.defaulttracer.tracers

import com.ybo.trackingplugin.tracerlib.Tracer

class TraceDefaultFactory : Tracer.Factory {
    override fun create(): Tracer {
        return SimplestDefaultTracer()
    }
}