package com.ybo.trackingplugin.tasks.utils

import com.ybo.trackingplugin.tasks.TraceProcessingParams

abstract class CodeGenerator {

    val tracePerformerPackage = TraceProcessingParams.TRACEPERFORMER_PACKAGE

    abstract fun generate(
        params: String,
        tracerFactoryString: String,
        insideMethodIndentation: String,
        methodName: String,
        tag: String,
        alterationOffset: Int,
    ): String
}
