package com.ybo.trackingplugin.tasks.utils.impl.patterns

/** what kind of a [PatternToSearch] we are dealing with */
sealed interface PatternName
sealed interface ParamsPatternName : PatternName

// params patterns
sealed interface KotlinParamPatternName : ParamsPatternName {
    object KotlinNormalParam : KotlinParamPatternName
    object KotlinHigherOrderParam : KotlinParamPatternName
}

sealed interface JavaParamPatternName : ParamsPatternName {
    object JavaNormalParam : JavaParamPatternName
}

// method patterns:
sealed interface MethodPatternNames : PatternName
sealed interface KotlinMethodPatternName : MethodPatternNames {
    object KotlinNormalMethod : KotlinMethodPatternName
    object KotlinExtensionFunction : KotlinMethodPatternName
    object KotlinHigherOrderFunctionWithParams : KotlinMethodPatternName
    object KotlinHigherOrderFunctionWithNoParams : KotlinMethodPatternName
}

sealed interface JavaMethodPatternName : MethodPatternNames {
    object JavaNormalMethod : JavaMethodPatternName
}
