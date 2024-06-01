package com.ybo.trackingplugin.extension

import com.ybo.trackingplugin.tasks.utils.createReverseTraceAnnotationConfig
import com.ybo.trackingplugin.tracerlib.defaulttracer.tracers.TraceDefaultFactory

data class TraceConfig(
    var name: String = "",
    var tracerFactory: String = TraceDefaultFactory::class.java.canonicalName,
    var annotation: String? = null,
) {
    /**
     * an "reverse" annotation that will be written in the code as a beacon for where a
     * trace annotation was present, to allow system to revert back to the annotated state (unprocessTrace)
     */
    fun alreadyProcessedAnnotation(): String? {
        return annotation?.let {
            createReverseTraceAnnotationConfig(it)
        }
    }
}
