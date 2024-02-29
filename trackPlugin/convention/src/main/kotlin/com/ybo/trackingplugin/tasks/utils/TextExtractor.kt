package com.ybo.trackingplugin.tasks.utils

import com.ybo.trackingplugin.tasks.data.TraceAnnotationMark
import com.ybo.trackingplugin.tasks.utils.impl.patterns.PatternName

/**
 * object than has some patterns to search in text, and extract their manifestation in the text
 * as objects.
 *It delivers them as a sorted list.
 */
class TextExtractor<ObjectToExtract, out T : PatternName>(
    private val patternProducer: PatternProducer<T>,
    private val patternSearcher: PatternSearcher<ObjectToExtract, T>,
    private val resultSorter: ResultSorter<ObjectToExtract> = NoSortSorter(),
) {

    fun extract(
        text: String,
        mark: TraceAnnotationMark? = null,
    ): List<ObjectToExtract> {
        return patternProducer
            .produce()
            .let { producedPatterns ->
                patternSearcher.search(text, producedPatterns, mark)
            }.flatMap { group ->
                group.results
            }.let {
                resultSorter.sort(it)
            }
    }

    private class NoSortSorter<T> : ResultSorter<T> {
        override fun sort(list: List<T>): List<T> {
            return list
        }
    }
}
