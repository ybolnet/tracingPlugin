package com.ybo.trackingplugin.tracerlib

/** object whose [Tracer.trace] method is called at each call of the methods annotated with the trace annotation*/
interface Tracer {

    /** called on the call of traced method. To be overriden by user of the plugin*/
    fun trace(
        defaultMessage: String,
        java: Boolean,
        method: Method,
        history: LimitedSizeList<Method>,
        parameterValues: Array<Any?>,
    )

    data class Method(
        val originalName: String,
        val possiblyObfuscatedMethod: String,
    )

    /** factory for Tracers. */
    interface Factory {
        /** instantiates a Tracer. */
        fun create(): Tracer
    }
}
