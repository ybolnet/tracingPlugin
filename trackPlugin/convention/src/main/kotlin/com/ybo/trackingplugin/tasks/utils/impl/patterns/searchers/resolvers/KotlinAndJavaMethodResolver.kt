package com.ybo.trackingplugin.tasks.utils.impl.patterns.searchers.resolvers

import com.ybo.trackingplugin.tasks.data.TracedMethod
import com.ybo.trackingplugin.tasks.utils.impl.patterns.JavaMethodPatternName
import com.ybo.trackingplugin.tasks.utils.impl.patterns.KotlinMethodPatternName
import com.ybo.trackingplugin.tasks.utils.impl.patterns.MethodPatternNames

internal class KotlinAndJavaMethodResolver(
    private val normalResolver: KotlinAndJavaMethodNormalResolver,
    private val higherOrderResolver: KotlinMethodHigherOrderResolver,
) :
    PatternResolver<TracedMethod, MethodPatternNames> {

    override fun resolve(
        matchResult: MatchResult,
        patternName: MethodPatternNames,
    ): TracedMethod {
        return when (patternName) {
            JavaMethodPatternName.JavaNormalMethod,
            KotlinMethodPatternName.KotlinNormalMethod,
            KotlinMethodPatternName.KotlinExtensionFunction,
            ->
                normalResolver.resolve(matchResult, patternName)

            KotlinMethodPatternName.KotlinHigherOrderFunctionWithParams ->
                higherOrderResolver.resolve(matchResult, patternName as KotlinMethodPatternName)
        }
    }
}
