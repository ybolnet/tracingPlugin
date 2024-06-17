package com.ybo.trackingplugin.tasks.utils.impl.patterns

import com.ybo.trackingplugin.tasks.data.TraceAnnotationMark

internal class KotlinMethodHigherOrderFunctionNoParamsPattern(
    override val markToLookFor: TraceAnnotationMark,
) : KotlinMethodPattern(markToLookFor) {

    override val name = KotlinMethodPatternName.KotlinHigherOrderFunctionWithNoParams

    override fun regex(): Regex {
        return Regex(
            ".*(\\w*).*" +
                "$fillerAnnotations*\\s*" +
                "(${getTraceAnnotation()})\\s+" +
                "$fillerAnnotations*\\s*" +
                "\\{[\\t ]*\\n" +
                "($insideIndentation)",
        )
    }
}
