package com.ybo.trackingplugin.tracerlib.defaulttracer.tracers

import com.ybo.trackingplugin.tracerlib.Tracer

/**
 * factory for tracer producing a already formatted trace for users who don't want to customize
 */
class TraceDefaultFactory : Tracer.Factory {
    override fun create(): Tracer {
        return object : Tracer {
            override fun trace(
                java: Boolean,
                method: Tracer.Method,
                history: List<Tracer.Method?>,
                parameterValues: Array<Any?>,
            ): Tracer.TraceHistoryManagementAction {
                println(getDefaultMessage(method, parameterValues))
                return Tracer.TraceHistoryManagementAction.None
            }
        }
    }
}
