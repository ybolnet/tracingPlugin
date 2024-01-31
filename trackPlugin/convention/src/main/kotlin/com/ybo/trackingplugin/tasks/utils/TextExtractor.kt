package com.ybo.trackingplugin.tasks.utils

class TextExtractor<ObjectToExtract>(
    private val patternProducer: PatternProducer,
    private val patternSearcher: PatternSearcher<ObjectToExtract>,
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
            }.flatMap {
                it.results
            }
    }
}
