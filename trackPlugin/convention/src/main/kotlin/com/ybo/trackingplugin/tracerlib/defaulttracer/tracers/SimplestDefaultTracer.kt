package com.ybo.trackingplugin.tracerlib.defaulttracer.tracers

import com.ybo.trackingplugin.tracerlib.Tracer

class SimplestDefaultTracer : Tracer {
    override fun trace(
        defaultMessage: String,
        java: Boolean,
        method: Tracer.Method,
        history: List<Tracer.Method>,
        parameterValues: Array<Any?>,
    ): Boolean {
        println("$defaultMessage method $method")
        return false
    }
}
