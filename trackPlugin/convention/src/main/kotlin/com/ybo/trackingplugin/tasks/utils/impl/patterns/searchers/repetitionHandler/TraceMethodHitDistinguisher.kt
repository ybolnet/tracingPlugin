package com.ybo.trackingplugin.tasks.utils.impl.patterns.searchers.repetitionHandler

import com.ybo.trackingplugin.tasks.data.TracedMethod

internal class TraceMethodHitDistinguisher : PatternHitDistinguisher<TracedMethod> {

    private val thingCountMap = mutableMapOf<TracedMethod, Int>()
    override fun generateUniqueId(patternHit: TracedMethod, text: String): Any {
        val count = thingCountMap.getOrDefault(patternHit, 0) + 1
        return MethodId(
            name = patternHit.name,
            line = calculateLine(text, patternHit.wholeSignature, count),
        )
    }

    private fun calculateLine(text: String, needle: String, n: Int): Int {
        val needleNb = countOccurrences(text, needle)
        return if (needleNb > 0 && needleNb >= n && n > 0) {
            val subtexts = text.split(needle)
            var nbLines = 0
            for (i in 0 until n) {
                val subText = subtexts[i]
                nbLines += subText.filter { it == '\n' }.length
            }
            nbLines += (n - 1) * needle.filter { it == '\n' }.length
            nbLines
        } else {
            -1
        }
    }

    private fun countOccurrences(text: String, substring: String): Int {
        return text.windowed(substring.length, 1) { it }.count { it == substring }
    }

    data class MethodId(
        val name: String,
        val line: Int,
    )
}
