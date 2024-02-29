package com.ybo.trackingplugin.tasks.utils.impl.patterns.searchers.repetitionHandler

internal interface PatternHitDistinguisher<SearchedResult> {
    fun generateUniqueId(
        patternHit: SearchedResult,
        text: String,
    ): Any
}
