package com.ybo.trackingplugin.tasks.utils.impl.patterns

import com.ybo.trackingplugin.tasks.data.PatternToSearch

class KotlinParamPattern : PatternToSearch<KotlinParamPatternName> {
    override val name: KotlinParamPatternName = KotlinParamPatternName.KotlinNormalParam

    override fun regex(): Regex {
        return Regex("\\b(\\w+)\\s*:\\s*([^,=]+(?:\\s*=\\s*[^,]+)?)")
    }
}
