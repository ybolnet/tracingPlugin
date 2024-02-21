package com.ybo.trackingplugin.tasks.utils

import com.ybo.trackingplugin.tasks.utils.impl.patterns.PatternName

class TextExtractor<ObjectToExtract, out T : PatternName>(
    private val patternProducer: PatternProducer<T>,
    private val patternSearcher: PatternSearcher<ObjectToExtract, T>,
    private val resultSorter: ResultSorter<ObjectToExtract> = NoSortSorter(),
) {

    fun extract(
        text: String,
    ): List<ObjectToExtract> {
        println("extracting here. ")
        return patternProducer
            .produce()
            .let { producedPatterns ->
                println("search pattern: $producedPatterns")
                patternSearcher.search(text, producedPatterns)
            }.flatMap { group ->
                println("process pattern $group")
                group.results
            }.let {
                println("sorting results $it")
                resultSorter.sort(it)
            }.also {
                println("sorted $it")
                println("sorted done")
            }
    }

    private class NoSortSorter<T> : ResultSorter<T> {
        override fun sort(list: List<T>): List<T> {
            return list
        }
    }
}
