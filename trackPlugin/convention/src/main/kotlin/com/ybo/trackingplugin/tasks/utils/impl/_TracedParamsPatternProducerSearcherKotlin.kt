package com.ybo.trackingplugin.tasks.utils.impl

import com.ybo.trackingplugin.tasks.data.PatternToSearch
import com.ybo.trackingplugin.tasks.data.TracedLanguage
import com.ybo.trackingplugin.tasks.data.TracedMethodParam

class _TracedParamsPatternProducerSearcherKotlin : _PatternProducerSearcher<TracedMethodParam>() {

    override fun produce(): List<PatternToSearch> {
        return listOf(NormalParams())
    }

    override fun createSearchedResult(
        matchResult: MatchResult,
        patternName: String,
    ): TracedMethodParam {
        return TracedMethodParam(matchResult.groupValues[1])
    }

    private class NormalParams : PatternToSearch {
        override val name = "KotlinNormalParams"
        override fun regex(): Regex {
            return Regex("\\b(\\w+)\\s*:\\s*([^,=]+(?:\\s*=\\s*[^,]+)?)")
        }
    }
    /*
    private fun normalParams(): PatternToSearch {
        return PatternToSearch(
            name = "KotlinNormalParams",
            language = TracedLanguage.KOTLIN,
            regex = Regex("\\b(\\w+)\\s*:\\s*([^,=]+(?:\\s*=\\s*[^,]+)?)"),
        )
    }*/
}
