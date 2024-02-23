package com.ybo.trackingplugin.tasks.utils.impl.patterns.searchers

import com.ybo.trackingplugin.tasks.data.PatternToSearch
import com.ybo.trackingplugin.tasks.utils.PatternSearcher
import com.ybo.trackingplugin.tasks.utils.impl.patterns.PatternName
import com.ybo.trackingplugin.tasks.utils.impl.patterns.searchers.resolvers.PatternResolver

internal open class BasePatternSearcher<out SearchedResult, in PatternType : PatternName>(
    private val resolver: PatternResolver<SearchedResult, PatternType>,
) : PatternSearcher<SearchedResult, PatternType> {

    override fun search(
        text: String,
        patterns: List<PatternToSearch<PatternType>>,
    ): List<PatternSearcher.GroupOfResult<SearchedResult>> {
        return patterns.mapNotNull { pattern ->
            val matcher = pattern.regex().findAll(text)
            if (matcher.count() == 0) {
                null
            } else {
                PatternSearcher.GroupOfResult(
                    patternName = pattern.name,
                    results = matcher
                        .map { resolver.resolve(it, pattern.name) }
                        .toList(),
                )
            }
        }
    }
}
