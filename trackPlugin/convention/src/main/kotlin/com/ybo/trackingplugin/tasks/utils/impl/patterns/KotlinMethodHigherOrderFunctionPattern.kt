package com.ybo.trackingplugin.tasks.utils.impl.patterns

import com.ybo.trackingplugin.tasks.data.TraceAnnotationMark

internal open class KotlinMethodHigherOrderFunctionPattern(
    override val markToLookFor: TraceAnnotationMark,
) : KotlinMethodPattern(markToLookFor) {

    override val name = KotlinMethodPatternName.KotlinHigherOrderFunctionWithParams
    override val paramsWithCapture: String = "([\\w\\s:\\.,\\(\\)]*)"
    open val arrow = "->"
    override fun regex(): Regex {
        return Regex(
            ".*(\\w*).*" +
                "$fillerAnnotations*\\s*" +
                "(${getTraceAnnotation()})\\s+" +
                "$fillerAnnotations*\\s*" +
                "\\{" +
                paramsWithCapture +
                "$arrow[\\t ]*\\n" +
                "($insideIndentation)",
        )
    }
}
