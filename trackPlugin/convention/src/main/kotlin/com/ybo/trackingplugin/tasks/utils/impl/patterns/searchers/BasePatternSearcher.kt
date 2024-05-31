package com.ybo.trackingplugin.tasks.utils.impl.patterns.searchers

import com.ybo.trackingplugin.TrackingPlugin
import com.ybo.trackingplugin.tasks.data.PatternToSearch
import com.ybo.trackingplugin.tasks.data.TraceAnnotationMark
import com.ybo.trackingplugin.tasks.utils.PatternSearcher
import com.ybo.trackingplugin.tasks.utils.impl.patterns.PatternName
import com.ybo.trackingplugin.tasks.utils.impl.patterns.searchers.repetitionHandler.PatternHitDistinguisher
import com.ybo.trackingplugin.tasks.utils.impl.patterns.searchers.resolvers.PatternResolver

internal open class BasePatternSearcher<out PatternHitType, in PatternType : PatternName>(
    private val resolver: PatternResolver<PatternHitType, PatternType>,
    private val hitDistinguisher: PatternHitDistinguisher<PatternHitType> = DummyHitDistinguisher(),
) : PatternSearcher<PatternHitType, PatternType> {

    override fun search(
        text: String,
        patterns: List<PatternToSearch<PatternType>>,
        mark: TraceAnnotationMark?,
    ): List<PatternSearcher.GroupOfResult<PatternHitType>> {
        val shouldCountHits = mark != null
        val maxNumberOfUniqueHits: Int? = calculateMaxNbOfHits(shouldCountHits, text, mark)
        var numberOfUniqueHits = 0
        val resultDic = mutableMapOf<Any, PatternHitType>()

        if (TrackingPlugin.DEBUG) println("max hits for ${mark?.shortVersion}: $maxNumberOfUniqueHits")
        return patterns.mapNotNull { pattern ->
            if (TrackingPlugin.DEBUG) println("checking pattern ${pattern.name}")
            val matcher = pattern.regex().findAll(text)
            val abortForEfficiency =
                shouldCountHits && (maxNumberOfUniqueHits!! <= numberOfUniqueHits)
            val startTime: Long = System.currentTimeMillis()
            if (abortForEfficiency || matcher.count() == 0) {
                if (TrackingPlugin.DEBUG) println("cost of regex ${pattern.name} : \n ${System.currentTimeMillis() - startTime}")
                if (TrackingPlugin.DEBUG) println("nothing for pattern ${pattern.name} (aborted? $abortForEfficiency)")
                null
            } else {
                if (TrackingPlugin.DEBUG) println("cost of regex ${pattern.name}  : \n ${System.currentTimeMillis() - startTime}")
                PatternSearcher.GroupOfResult(
                    patternName = pattern.name,
                    results = matcher
                        .map { match ->
                            resolver
                                .resolve(match, pattern.name)
                                .also {
                                    val key = hitDistinguisher.generateUniqueId(it, text)
                                    val alreadyEncounteredHit = resultDic.containsKey(key)
                                    if (!alreadyEncounteredHit) {
                                        numberOfUniqueHits++
                                        resultDic[key] = it
                                    }
                                }
                        }.toList(),
                )
            }.also {
                if (TrackingPlugin.DEBUG) println("found hits $numberOfUniqueHits for now")
            }
        }
    }

    private fun calculateMaxNbOfHits(
        shouldCountHits: Boolean,
        text: String,
        mark: TraceAnnotationMark?,
    ): Int? {
        return if (shouldCountHits) {
            countOccurrences(
                text = text,
                substring = mark!!.shortVersion,
            ) + countOccurrences(
                text = text,
                substring = mark.longVersion,
            )
        } else {
            null
        }
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

    private class DummyHitDistinguisher<T> : PatternHitDistinguisher<T> {
        override fun generateUniqueId(patternHit: T, text: String): Any {
            return patternHit as Any
        }

        data class DefaultKey(private val int: Int)
    }
}
