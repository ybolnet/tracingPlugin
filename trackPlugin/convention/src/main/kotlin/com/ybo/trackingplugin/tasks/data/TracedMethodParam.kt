package com.ybo.trackingplugin.tasks.data

import com.ybo.trackingplugin.tasks.utils.impl.patterns.PatternName

data class TracedMethodParam(
    val name: String,
    val patternType: PatternName,
)
