package com.ybo.trackingplugin.extension

data class TraceConfig(
    var name: String = "",
    var tracerFactory: String = "",
    var exclude: Array<String>? = null,
    var toBeProcessedAnnotation: String? = null,
    var alreadyProcessedAnnotation: String? = null,
    var srcPath: String? = null,
)
