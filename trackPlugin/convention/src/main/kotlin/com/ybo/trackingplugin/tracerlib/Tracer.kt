package com.ybo.trackingplugin.tracerlib

/** object whose [Tracer.trace] method is called at each call of the methods annotated with the trace annotation*/
interface Tracer {

    /** called on the call of traced method. To be overriden by user of the plugin.
     * Note that history is maxed at 100. After that the oldest item is discarded.
     *
     * returns true if we want to empty the history after tracing the current method
     * */
    fun trace(
        defaultMessage: String,
        java: Boolean,
        method: Method,
        history: List<Method>,
        parameterValues: Array<Any?>,
    ): Boolean

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
