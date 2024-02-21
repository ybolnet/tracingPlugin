package com.ybo.trackingplugin.tasks.utils.impl.patterns.searchers.resolvers

import com.ybo.trackingplugin.tasks.data.TracedMethod
import com.ybo.trackingplugin.tasks.utils.impl.patterns.MethodPatternNames

internal class KotlinAndJavaMethodNormalResolver :
    PatternResolver<TracedMethod, MethodPatternNames> {

    override fun resolve(
        matchResult: MatchResult,
        patternName: MethodPatternNames,
    ): TracedMethod {
        return TracedMethod(
            wholeSignature = matchResult.groupValues[0],
            indentationInsideMethod = if (matchResult.groupValues.size > 4) matchResult.groupValues[4] else " ",
            paramBlock = matchResult.groupValues[3],
            name = matchResult.groupValues[2],
            patternType = patternName,
        )
    }
}
