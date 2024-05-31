package com.ybo.trackingplugin.tasks.data

import com.ybo.trackingplugin.tasks.utils.impl.patterns.MethodPatternNames

/**
 * method found associated with a signal ([TrackedSignal]) in a text
 */
data class TracedMethod(
    val wholeSignature: String,
    val indentationInsideMethod: String,
    val paramBlock: String,
    val name: String = "",
    val patternType: MethodPatternNames,
    val line: Int = 0,
)
