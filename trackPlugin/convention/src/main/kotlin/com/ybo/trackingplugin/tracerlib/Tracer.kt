package com.ybo.trackingplugin.tracerlib

/** object whose [Tracer.trace] method is called at each call of the methods annotated with the trace annotation*/
interface Tracer {

    /** called on the call of traced method. To be overriden by user of the plugin.
     * Note that history is maxed at 100. After that the oldest item is discarded.
     *
     * returns a value ([TraceHistoryManagementAction])saying what we should do with the history of calls (see history parameter)
     * when the tracing is done.
     * (for instance we might want to keep the history of call in memory under a certain size to avoid memory problems)
     * */
    fun trace(
        defaultMessage: String,
        java: Boolean,
        annotationName: TraceAnnotationName,
        method: Method,
        history: List<Method?>,
        parameterValues: Array<Any?>,
    ): TraceHistoryManagementAction

    @JvmInline
    value class TraceAnnotationName(val value: String)

    /**
     * what to do with history after a trace() call.
     */
    sealed interface TraceHistoryManagementAction {
        /** leave history as it is*/
        object None : TraceHistoryManagementAction

        /** clear history*/
        object Clear : TraceHistoryManagementAction

        /** delete oldest entry in history */
        object DeleteOldest : TraceHistoryManagementAction

        /** null the entry at a specific index */
        data class NullAtIndex(val index: Int) : TraceHistoryManagementAction
    }

    /**
     * [TraceHistoryManagementAction.DeleteOldest] (removing oldest entry in history)
     * if the history is too big (taking in account null values),
     * [TraceHistoryManagementAction.None] (no action) otherwise
     */
    fun TraceHistoryManagementAction.DeleteOldest.ifOverLimitSize(
        history: List<Method?>,
        maxSize: Int,
    ): TraceHistoryManagementAction {
        return if (history.size >= maxSize) {
            this
        } else {
            TraceHistoryManagementAction.None
        }
    }

    /**
     * [TraceHistoryManagementAction.DeleteOldest] (removing oldest entry in history)
     * if the history is too big (ignoring null values),
     * [TraceHistoryManagementAction.None] (no action) otherwise
     */
    fun TraceHistoryManagementAction.DeleteOldest.ifNoNullsOverLimitSize(
        history: List<Method?>,
        maxSize: Int,
    ): TraceHistoryManagementAction {
        return if (history.mapNotNull { it }.size >= maxSize) {
            this
        } else {
            TraceHistoryManagementAction.None
        }
    }

    /**
     * only the name of the annotation without the package path
     */
    fun TraceAnnotationName.shortName(): String {
        return value.run {
            val lastDotIndex = lastIndexOf('.')
            if (lastDotIndex == -1) {
                this // If no dot found, return the original string
            } else {
                substring(lastDotIndex + 1)
            }
        }
    }

    data class Method(
        val originalName: String,
        val possiblyObfuscatedMethod: String,
        val link: String = "",
    )

    /** factory for Tracers. */
    interface Factory {
        /** instantiates a Tracer. */
        fun create(): Tracer
    }
}
