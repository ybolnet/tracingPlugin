package com.ybo.trackingplugin.tasks.data

import com.ybo.trackingplugin.tasks.utils.impl.patterns.MethodPatternNames

data class TracedMethod(
    val wholeSignature: String,
    val indentationInsideMethod: String,
    val paramBlock: String,
    val name: String = "",
    val patternType: MethodPatternNames,
)
