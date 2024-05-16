package com.ybo.trackingplugin.extension

data class ReturnTraceConfig(
    var tracerFactory: String = "",
    var exclude: Array<String>? = null,
    var srcPath: String? = null,
)
