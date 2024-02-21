package com.ybo.trackingplugin.tasks.utils.impl.patterns

import com.ybo.trackingplugin.tasks.data.TraceAnnotationMark

/**
 * methods like fun Type.method(maybeParam) { MANDATORY_NEW_LINE
 */
internal open class KotlinMethodExtensionFunctionPattern(
    override val markToLookFor: TraceAnnotationMark,
) : KotlinMethodPattern(markToLookFor) {
    override val name = KotlinMethodPatternName.KotlinExtensionFunction
    override val methodName: String = "[\\w\\.]+"
}
