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
        java: Boolean,
        method: Method,
        history: List<Method?>,
        parameterValues: Array<Any?>,
    ): TraceHistoryManagementAction

    /** name of the annotation tracing a mthod */
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

        /** nulls oldest entry in history */
        object NullOldest : TraceHistoryManagementAction

        /** null the entry at a specific index */
        data class NullAtIndex(val index: Int) : TraceHistoryManagementAction
    }

    /**
     * returns receiver
     * if the history is too big (nb of non null values), or
     * [TraceHistoryManagementAction.None] (no action) otherwise
     */
    fun TraceHistoryManagementAction.ifOverLimit(
        history: List<Method?>,
        maxSize: Int,
    ): TraceHistoryManagementAction {
        return if (history.nbNoNullValues() >= maxSize) {
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
        val annotation: TraceAnnotationName,
    )

    /** factory for Tracers. */
    interface Factory {
        /** instantiates a Tracer. */
        fun create(): Tracer
    }

    /** constructs an already formatted message for trace */
    fun getDefaultMessage(
        method: Tracer.Method,
        parameterValues: Array<Any?>,
    ): String {
        return "${method.asLoggable()} " +
            "(${
                parameterValues.fold("") { acc, item -> acc + item + "," }
            })" +
            " ${method.annotation.asTag()} $TAG"
    }

    fun TraceAnnotationName.asTag(): String {
        return "#feature:" + this.shortName()
    }

    fun Tracer.Method.asLoggable(): String {
        return "$link - ${originalName.lastPart()}"
    }

    fun String.lastPart(): String {
        val lastDotIndex = lastIndexOf('.')
        return if (lastDotIndex == -1) {
            this // If no dot found, return the original string
        } else {
            substring(lastDotIndex + 1)
        }
    }

    private fun List<Method?>.nbNoNullValues(): Int {
        return mapNotNull { it }.size
    }

    private fun List<Method?>.onlyNullValues(): Boolean = nbNoNullValues() == 0

    companion object {
        const val TAG = "#Trace"
    }
}
