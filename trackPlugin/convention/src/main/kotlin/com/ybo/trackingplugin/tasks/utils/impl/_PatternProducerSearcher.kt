package com.ybo.trackingplugin.tasks.utils.impl

import com.ybo.trackingplugin.tasks.data.PatternToSearch
import com.ybo.trackingplugin.tasks.utils.PatternProducer
import com.ybo.trackingplugin.tasks.utils.PatternSearcher

abstract class _PatternProducerSearcher<SearchedResult> :
    PatternProducer,
    PatternSearcher<SearchedResult> {

    abstract fun createSearchedResult(
        matchResult: MatchResult,
        patternName: String,
    ): SearchedResult

    override fun search(
        text: String,
        patterns: List<PatternToSearch>,
    ): List<PatternSearcher.GroupOfResult<SearchedResult>> {
        return patterns.mapNotNull { pattern ->
            val matcher = pattern.regex().findAll(text)
            if (matcher.count() == 0) {
                println("nothing interesting here")
                null
            } else {
                PatternSearcher.GroupOfResult(
                    patternName = pattern.name,
                    results = matcher
                        .map { createSearchedResult(it, pattern.name) }
                        .toList(),
                )
            }
        }
    }
}
