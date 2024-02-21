package com.ybo.trackingplugin.tasks.utils

import com.ybo.trackingplugin.tasks.data.PatternToSearch
import com.ybo.trackingplugin.tasks.utils.impl.patterns.PatternName
import com.ybo.trackingplugin.tasks.utils.impl.patterns.searchers.resolvers.PatternResolver

/** searches patterns ([PatternToSearch]) in text and returns found data objects.
 * the data objects are usually created by a [PatternResolver]
 * the patterns are usually created by a [PatternProducer]
 * */
interface PatternSearcher<out ResultType, in PatternType : PatternName> {

    fun search(
        text: String,
        patterns: List<PatternToSearch<PatternType>>,
    ): List<GroupOfResult<ResultType>>

    data class GroupOfResult<out ResultType>(
        val patternName: PatternName,
        val results: List<ResultType>,
    )
}
