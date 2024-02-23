package com.ybo.trackingplugin.tasks.utils.impl.patterns.searchers.resolvers

import com.ybo.trackingplugin.tasks.data.TracedMethod
import com.ybo.trackingplugin.tasks.utils.impl.patterns.KotlinMethodPatternName

internal class KotlinMethodHigherOrderNoParamsResolver : PatternResolver<TracedMethod, KotlinMethodPatternName> {

    override fun resolve(
        matchResult: MatchResult,
        patternName: KotlinMethodPatternName,
    ): TracedMethod {
        return TracedMethod(
            wholeSignature = matchResult.groupValues[0],
            indentationInsideMethod = if (matchResult.groupValues.size > 3) matchResult.groupValues[3] else " ",
            paramBlock = "",
            name = "",
            patternType = patternName,
        )
    }

}
