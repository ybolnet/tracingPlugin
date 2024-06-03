package com.ybo.trackingplugin.tracerlib.defaulttracer.tracers

import com.ybo.trackingplugin.tracerlib.Tracer
import com.ybo.trackingplugin.tracerlib.Tracer.TraceHistoryManagementAction.None
import com.ybo.trackingplugin.tracerlib.Tracer.TraceHistoryManagementAction.NullOldest

open class SimpleTracer(private val historyMaxSize: Int? = 300) : Tracer {
    override fun trace(
        java: Boolean,
        method: Tracer.Method,
        history: List<Tracer.Method?>,
        parameterValues: Array<Any?>,
    ): Tracer.TraceHistoryManagementAction {
        println(getDefaultMessage(method, parameterValues))
        return historyMaxSize?.let {
            NullOldest.ifOverLimit(history, historyMaxSize)
        } ?: None
    }
}
