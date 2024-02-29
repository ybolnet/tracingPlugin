package com.ybo.trackingplugin.tasks.utils.impl.patterns

internal class KotlinMethodPatternDecorator(
    private val substrate: KotlinMethodPattern,
    private val nameReplacement: KotlinMethodPatternName? = null,
    private val paramsWithCaptureReplacement: String? = null,
) : KotlinMethodPattern(substrate.markToLookFor) {

    override val name: KotlinMethodPatternName = nameReplacement ?: super.name
    override val paramsWithCapture: String = paramsWithCaptureReplacement ?: super.paramsWithCapture
}
