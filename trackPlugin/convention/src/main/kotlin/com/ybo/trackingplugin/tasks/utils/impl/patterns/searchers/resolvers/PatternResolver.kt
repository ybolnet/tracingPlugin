package com.ybo.trackingplugin.tasks.utils.impl.patterns.searchers.resolvers

import com.ybo.trackingplugin.tasks.data.PatternToSearch
import com.ybo.trackingplugin.tasks.utils.impl.patterns.PatternName

/** transforms the matchResult created by the finding of a [PatternToSearch] in text,
 * into a resulting data object*/
internal interface PatternResolver<ResultType, in PatternType : PatternName> {

    fun resolve(
        matchResult: MatchResult,
        patternName: PatternType,
    ): ResultType
}
