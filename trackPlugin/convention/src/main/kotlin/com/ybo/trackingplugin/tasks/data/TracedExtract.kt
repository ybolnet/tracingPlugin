package com.ybo.trackingplugin.tasks.data

/**
 * result of a searching of signal  ([TrackedSignal])in a text.
 */
data class TracedExtract(
    val signal: TrackedSignal,
    val method: TracedMethod,
    val params: List<TracedMethodParam>,
)
