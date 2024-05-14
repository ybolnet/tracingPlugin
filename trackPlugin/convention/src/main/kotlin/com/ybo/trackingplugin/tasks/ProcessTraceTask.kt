package com.ybo.trackingplugin.tasks

import com.ybo.trackingplugin.TrackingPlugin
import com.ybo.trackingplugin.tasks.data.TraceAnnotationMark
import com.ybo.trackingplugin.tasks.data.TracedMethod
import com.ybo.trackingplugin.tasks.utils.TextExtractor
import com.ybo.trackingplugin.tasks.utils.createCodeGenerator
import com.ybo.trackingplugin.tasks.utils.createPatternProducerForTracedMethods
import com.ybo.trackingplugin.tasks.utils.createPatternProducerForTracedParams
import com.ybo.trackingplugin.tasks.utils.createPatternSearcherForTracedMethods
import com.ybo.trackingplugin.tasks.utils.createPatternSearcherForTracedParams
import com.ybo.trackingplugin.tasks.utils.impl.patterns.sorters.MethodsSorter
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.File

/** task adding a trace call at the start of every traced method.
 * counterpart of [UnprocessTraceTask]*/
open class ProcessTraceTask : BrowsingTask() {

    @TaskAction
    fun processTrace() {
        alterationsMap = mutableMapOf<String, Int>()
        startTime = System.currentTimeMillis()
        val residuals = mutableListOf<Residual>()
        browseCode { tracked, config ->
            if (TrackingPlugin.DEBUG) println("starting browsing took " + (System.currentTimeMillis() - startTime))
            startTime = System.currentTimeMillis()
            if (config.srcPath == null) {
                throw GradleException("srcPath must be defined")
            }
            processTraceAnnotations(
                tracked.file,
                tracked.toBeProcessedMarkToTrack,
                tracked.alreadyProcessedMarkToTrack,
                config.tracerFactory,
            ).also {
                if (it.nbMarks != 0) {
                    residuals.add(it)
                }
            }
        }
        residuals
            .fold("") { acc, residual ->
                acc + residual.nbMarks +
                    " remaining unprocessed marks in " + residual.filename + ",\n "
            }
            .let {
                if (it.isNotBlank()) {
                    println(" /!\\ final check problem : $it")
                }
            }
    }

    private fun processTraceAnnotations(
        file: File,
        mark: TraceAnnotationMark,
        processed: TraceAnnotationMark,
        tracerFactortStr: String,
    ): Residual {
        val tag = TraceProcessingParams.TAG
        var text = file.readText()
        if (TrackingPlugin.DEBUG) println("reading file ${file.name} took " + (System.currentTimeMillis() - startTime))
        startTime = System.currentTimeMillis()
        val codeGenerator = createCodeGenerator(mark.language)
        var indexOfTraceInFile = 0
        extractMethods(text, mark) { method ->
            if (TrackingPlugin.DEBUG) println("processing method ${method.name} pattern ${method.patternType} ${mark.shortVersion} ")
            try {
                val paramsExtractor = TextExtractor(
                    patternProducer = createPatternProducerForTracedParams(
                        mark.language,
                        method.patternType,
                    ),
                    patternSearcher = createPatternSearcherForTracedParams(
                        mark.language,
                        method.patternType,
                    ),
                )
                startTime = System.currentTimeMillis()
                val paramsStr = paramsExtractor
                    .extract(method.paramBlock)
                    .joinToString(", ") { it.name }
                if (TrackingPlugin.DEBUG) println("extracting params " + (System.currentTimeMillis() - startTime))

                val newLine = (method.wholeSignature + "")
                    .replace(mark.shortVersion, processed.longVersion)
                    .replace(mark.longVersion, processed.longVersion)

                val alto = getMethodAlterationOffset(method, file)
                if (TrackingPlugin.DEBUG) println("alterationOffset = $alto + $indexOfTraceInFile for file ${file.name} and method ${method.name}")
                val alterationOffsetForThisMethod =
                    getMethodAlterationOffset(method, file) + indexOfTraceInFile
                text = text.replace(
                    method.wholeSignature,
                    newLine + codeGenerator.generate(
                        params = paramsStr,
                        tracerFactoryString = tracerFactortStr,
                        insideMethodIndentation = method.indentationInsideMethod,
                        methodName = method.name,
                        tag = tag,
                        alterationOffset = alterationOffsetForThisMethod,
                    ),
                )

                indexOfTraceInFile++
                setMethodAlterationOffset(method, file, alterationOffsetForThisMethod)
            } catch (error: GradleException) {
                error.printStackTrace()
                if (TrackingPlugin.DEBUG) println("skipping method ${method.name}...")
            }
        }
        file.writeText(text)
        if (TrackingPlugin.DEBUG) println("processing trace done for file " + file.name)
        return Residual(file.name, nbOfResidualMarks(mark, text))
    }

    private fun extractMethods(
        text: String,
        mark: TraceAnnotationMark,
        onMethod: (method: TracedMethod) -> Unit,
    ) {
        val methodExtractor = TextExtractor(
            patternProducer = createPatternProducerForTracedMethods(mark),
            patternSearcher = createPatternSearcherForTracedMethods(mark),
            resultSorter = MethodsSorter(text),
        )
        val tracedMethods = methodExtractor.extract(text, mark)
        if (TrackingPlugin.DEBUG) println("extracting took " + (System.currentTimeMillis() - startTime))
        if (tracedMethods.isEmpty()) {
            if (TrackingPlugin.DEBUG) println("no traced methods")
            return
        }

        for (method in tracedMethods) {
            if (TrackingPlugin.DEBUG) println("processing method ${method.name} pattern ${method.patternType} ${mark.shortVersion} ")
            onMethod(method)
        }
    }

    private fun setMethodAlterationOffset(method: TracedMethod, file: File, offset: Int) {
        val key = method.wholeSignature + file.name
        alterationsMap[key] = offset
    }

    private fun getMethodAlterationOffset(method: TracedMethod, file: File): Int {
        val key = method.wholeSignature + file.name
        return alterationsMap[key] ?: 0
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

    /** represents a case of a annotation mark that was not recognized as a tracked method */
    data class Residual(val filename: String, val nbMarks: Int)

    companion object {
        var alterationsMap = mutableMapOf<String, Int>()
        private var startTime: Long = 0L
    }
}
