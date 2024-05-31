package com.ybo.trackingplugin.tasks.utils

import com.ybo.trackingplugin.TrackingPlugin
import com.ybo.trackingplugin.tasks.ProcessTraceTask
import com.ybo.trackingplugin.tasks.ProcessTraceTask.SignalProcessor.ReplacementIntent
import com.ybo.trackingplugin.tasks.data.Residual
import com.ybo.trackingplugin.tasks.data.TraceAnnotationMark
import com.ybo.trackingplugin.tasks.data.TracedExtract
import com.ybo.trackingplugin.tasks.data.TracedLanguage
import com.ybo.trackingplugin.tasks.data.TracedMethod
import com.ybo.trackingplugin.tasks.data.TracedMethodParam
import com.ybo.trackingplugin.tasks.data.TrackedFile
import com.ybo.trackingplugin.tasks.data.TrackedSignal
import com.ybo.trackingplugin.tasks.utils.impl.patterns.MethodPatternNames
import com.ybo.trackingplugin.tasks.utils.impl.patterns.ParamsPatternName

internal class SignalProcessorImpl(
    private val createPatternProducerForMethods: PatternProducerForMethodsFactory,
    private val createPatternSearcherForMethods: PatternSearcherForMethodsFactory,
    private val createMethodSorter: (text: String) -> ResultSorter<TracedMethod>,
    private val createPatternProducerForParams: PatternProducerForParamsFactory,
    private val createPatternSearcherForParams: PatternSearcherForParamsFactory,
) : ProcessTraceTask.SignalProcessor {

    override fun processSignalsInFile(
        trackedFile: TrackedFile,
        processExtract: (extract: TracedExtract) -> ReplacementIntent,
    ): Residual {
        val timing = Timing()
        val file = trackedFile.file
        timing.start("process signal for file ${file.name}")
        var text = file.readText()
        val signals = trackedFile.signals
        var nbResidual = 0
        extractMethods(text, signals).forEach { extract ->
            timing.start("treat extract $extract")
            processExtract(extract).also { replacementIntent ->
                if (replacementIntent.targetInFileText.isNotEmpty()) {
                    text = text.replace(
                        oldValue = replacementIntent.targetInFileText,
                        newValue = replacementIntent.replacement,
                    )
                }
            }
            timing.end("treat extract $extract")
        }
        signals.forEach {
            nbResidual += nbOfResidualMarks(
                mark = it.toBeProcessedMarkToTrack,
                text = text,
            )
        }
        timing.start("writing text")
        file.writeText(text)
        timing.end("writing text")

        timing.end("process signal for file ${file.name}")
        return Residual(file.name, nbResidual)
    }

    /**
     * lists the hits matching the signals in parameter, in the text in parameter
     */
    private fun extractMethods(
        text: String,
        signals: List<TrackedSignal>,
    ): List<TracedExtract> {
        val timing = Timing()
        timing.start("extraction")
        return signals.flatMap { signal ->
            val methodExtractor = TextExtractor(
                patternProducer = createPatternProducerForMethods(signal.toBeProcessedMarkToTrack),
                patternSearcher = createPatternSearcherForMethods(signal.toBeProcessedMarkToTrack),
                resultSorter = createMethodSorter(text),
            )
            timing.start("extracting methods for signal $signal")
            val tracedMethods = methodExtractor.extract(text, signal.toBeProcessedMarkToTrack)
            timing.end("extracting methods for signal $signal")
            tracedMethods.map { method ->
                timing.start("extracting params for $method")
                val listParams = TextExtractor(
                    patternProducer = createPatternProducerForParams(
                        signal.toBeProcessedMarkToTrack.language,
                        method.patternType,
                    ),
                    patternSearcher = createPatternSearcherForParams(
                        signal.toBeProcessedMarkToTrack.language,
                        method.patternType,
                    ),
                ).extract(method.paramBlock)
                timing.end("extracting params for $method")
                if (TrackingPlugin.DEBUG) println("extracting params " + (System.currentTimeMillis() - startTime))
                TracedExtract(
                    signal = signal,
                    method = method,
                    params = listParams,
                )
            }
        }.sortedBy { extract ->
            extract.method.run {
                line.toFloat() + (wholeSignature.length.coerceAtMost(1000).toFloat() / 1000F)
            }
        }.also {
            timing.end("extraction")
        }
    }

    /** calculates number of [Residual]s when a file has been treated.*/
    private fun nbOfResidualMarks(
        mark: TraceAnnotationMark,
        text: String,
    ): Int {
        val extendedFormNb = text.split(mark.longVersion, ignoreCase = true).size - 1
        val contractedFormNb = text.split(mark.shortVersion, ignoreCase = true).size - 1
        return extendedFormNb + contractedFormNb
    }

    companion object {
        private var startTime: Long = 0L
    }
}

typealias PatternSearcherForMethodsFactory = (markToLookFor: TraceAnnotationMark) -> PatternSearcher<TracedMethod, MethodPatternNames>
typealias PatternProducerForMethodsFactory = (markToLookFor: TraceAnnotationMark) -> PatternProducer<MethodPatternNames>
typealias PatternProducerForParamsFactory = (
    language: TracedLanguage,
    methodPatternType: MethodPatternNames,
) -> PatternProducer<ParamsPatternName>

typealias PatternSearcherForParamsFactory = (
    language: TracedLanguage,
    methodPatternType: MethodPatternNames,
) -> PatternSearcher<TracedMethodParam, ParamsPatternName>
