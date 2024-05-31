package com.ybo.trackingplugin.tasks

import com.ybo.trackingplugin.tasks.data.Residual
import com.ybo.trackingplugin.tasks.data.TracedExtract
import com.ybo.trackingplugin.tasks.data.TrackedFile
import com.ybo.trackingplugin.tasks.data.TrackedSignal
import com.ybo.trackingplugin.tasks.utils.Timing
import com.ybo.trackingplugin.tasks.utils.createCodeGenerator
import com.ybo.trackingplugin.tasks.utils.createSignalProcessor
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/** task adding a trace call at the start of every traced method.
 * counterpart of [UnprocessTraceTask]*/
open class ProcessTraceTask : BrowsingTask() {

    /**
     * find signals in a file, and process them (ie add a tracer in the methods associated with the signals)
     */
    interface SignalProcessor {
        /**
         * starts the processing of the signals  ([TrackedSignal]) in the tracked file,
         * while leaving for the caller the specifics about how to process each
         * extract found in file text.
         * Returns a [Residual] a conclusion object summarizing how many suspected
         * annotation intents were not successfully processed, or matched with a signal
         */
        fun processSignalsInFile(
            trackedFile: TrackedFile,
            processExtract: (extract: TracedExtract) -> ReplacementIntent,
        ): Residual

        /**
         * represents an order to replace [ReplacementIntent.targetInFileText]
         * with [ReplacementIntent.replacement] in the file being processed
         */
        data class ReplacementIntent(
            val targetInFileText: String,
            val replacement: String,
        )
    }

    @TaskAction
    fun processTrace() {
        val residuals = mutableListOf<Residual>()
        val signalProcessor: SignalProcessor = createSignalProcessor()
        forEachFile { trackedFile ->
            processFile(trackedFile, signalProcessor).also { residual ->
                if (residual.nbMarks != 0) {
                    residuals.add(residual)
                }
            }
        }
        residuals.forEach {
            logger.warn("${it.nbMarks} trace annotations mentions remaining unprocessed in ${it.filename}")
        }
    }

    private fun processFile(trackedFile: TrackedFile, signalProcessor: SignalProcessor): Residual {
        var previousResultOfProcess: Residual? = null
        var resultOfProcess: Residual? = null
        while (!processShouldNotRepeat(previousResultOfProcess, resultOfProcess)) {
            previousResultOfProcess = resultOfProcess
            resultOfProcess =
                signalProcessor.processSignalsInFile(trackedFile, this::processExtract)
        }
        return resultOfProcess!!
    }

    private fun processExtract(extract: TracedExtract): SignalProcessor.ReplacementIntent {
        val timing = Timing()
        val tag = TraceProcessingParams.TAG
        timing.start("treat extract $extract")
        val method = extract.method
        val mark = extract.signal.toBeProcessedMarkToTrack
        val processed = extract.signal.alreadyProcessedMarkToTrack
        val params = extract.params
        val tracerFactortStr = extract.signal.tracerFactory
        return try {
            val codeGenerator = createCodeGenerator(mark.language)
            val newLine = (method.wholeSignature + "")
                .replace(mark.shortVersion, processed.longVersion)
                .replace(mark.longVersion, processed.longVersion)
            SignalProcessor.ReplacementIntent(
                targetInFileText = method.wholeSignature,
                replacement = newLine + codeGenerator.generate(
                    params = params.joinToString(", ") { it.name },
                    tracerFactoryString = tracerFactortStr,
                    insideMethodIndentation = method.indentationInsideMethod,
                    methodName = method.name,
                    tag = tag,
                    line = method.line,
                    mark = mark,
                ),
            )
        } catch (error: GradleException) {
            error.printStackTrace()
            SignalProcessor.ReplacementIntent("", "")
        }.also {
            timing.end("treat extract $extract")
        }
    }

    private fun processShouldNotRepeat(
        previousResultOfProcess: Residual?,
        resultOfProcess: Residual?,
    ): Boolean {
        val allWasProcessed = resultOfProcess?.let {
            it.nbMarks == 0
        } ?: false
        val noMoreCanBeDone = resultOfProcess?.let { currentResult ->
            previousResultOfProcess?.let { previousResult ->
                currentResult == previousResult
            } ?: false
        } ?: false
        return allWasProcessed || noMoreCanBeDone
    }
}
