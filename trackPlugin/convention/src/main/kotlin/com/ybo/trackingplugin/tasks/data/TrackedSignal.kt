package com.ybo.trackingplugin.tasks.data

/**
 * represents a "topic" that will be the subject of tracing in the client code base.
 * It consists in the annotation that will be associated with some methods user want to trace,
 * associated with a few other info with that.
 */
data class TrackedSignal(
    /** annotation signaling that the associated method should be processed*/
    val toBeProcessedMarkToTrack: TraceAnnotationMark,
    /** annotation signaling that the associated method has be already processed */
    val alreadyProcessedMarkToTrack: TraceAnnotationMark,
    /** name of the factory that should create a tracer for this signal*/
    var tracerFactory: String,
)
