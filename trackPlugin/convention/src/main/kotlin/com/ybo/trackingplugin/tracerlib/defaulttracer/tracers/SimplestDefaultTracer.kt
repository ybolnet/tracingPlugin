package com.ybo.trackingplugin.tracerlib.defaulttracer.tracers

import com.ybo.trackingplugin.tracerlib.Tracer
import com.ybo.trackingplugin.tracerlib.Tracer.TraceHistoryManagementAction
import com.ybo.trackingplugin.tracerlib.Tracer.TraceHistoryManagementAction.None

class SimplestDefaultTracer : Tracer {
    override fun trace(
        defaultMessage: String,
        java: Boolean,
        method: Tracer.Method,
        history: List<Tracer.Method?>,
        parameterValues: Array<Any?>,
    ): TraceHistoryManagementAction {
        println("$defaultMessage method $method")
        return None
    }
}
