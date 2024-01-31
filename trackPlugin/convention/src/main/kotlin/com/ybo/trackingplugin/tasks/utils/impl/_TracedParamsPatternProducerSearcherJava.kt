package com.ybo.trackingplugin.tasks.utils.impl

import com.ybo.trackingplugin.tasks.data.PatternToSearch
import com.ybo.trackingplugin.tasks.data.TracedLanguage
import com.ybo.trackingplugin.tasks.data.TracedMethodParam

class _TracedParamsPatternProducerSearcherJava : _PatternProducerSearcher<TracedMethodParam>() {

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
        override val name = "JavaNormalParams"
        override fun regex(): Regex {
            return Regex("(?:@(?:\\w+(?:\\n*\\.\\n*)?)+(?:\\(.*\\))*\\s*)?(?:final\\s)?[\\w.]+\\s+(\\w+)\\s*,?")
        }
    }
    /*
    private fun normalParams(): PatternToSearch {
        return PatternToSearch(
            name = "JavaNormalParams",
            language = TracedLanguage.JAVA,
            regex = Regex("(?:@(?:\\w+(?:\\n*\\.\\n*)?)+(?:\\(.*\\))*\\s*)?(?:final\\s)?[\\w.]+\\s+(\\w+)\\s*,?"),
        )
    }*/
}
