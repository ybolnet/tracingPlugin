package com.ybo.trackingplugin.tasks.utils

import com.ybo.trackingplugin.tasks.TraceProcessingParams

/**
 * generator of the code line that will be added at the start of tracked methods
 */
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
