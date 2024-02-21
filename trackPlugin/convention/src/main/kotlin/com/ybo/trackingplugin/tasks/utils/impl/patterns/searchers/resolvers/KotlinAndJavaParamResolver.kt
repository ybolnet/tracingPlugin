package com.ybo.trackingplugin.tasks.utils.impl.patterns.searchers.resolvers

import com.ybo.trackingplugin.tasks.data.TracedMethodParam
import com.ybo.trackingplugin.tasks.utils.impl.patterns.ParamsPatternName

internal class KotlinAndJavaParamResolver :
    PatternResolver<TracedMethodParam, ParamsPatternName> {

    override fun resolve(
        matchResult: MatchResult,
        patternName: ParamsPatternName,
    ): TracedMethodParam {
        return TracedMethodParam(
            name = matchResult.groupValues[1],
            patternType = patternName,
        )
    }
}
