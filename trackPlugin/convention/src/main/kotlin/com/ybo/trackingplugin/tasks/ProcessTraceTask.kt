package com.ybo.trackingplugin.tasks

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
        browseCode { tracked, config ->
            if (config.srcPath == null) {
                throw GradleException("srcPath must be defined")
            }
            processTraceAnnotations(
                tracked.file,
                tracked.toBeProcessedMarkToTrack,
                tracked.alreadyProcessedMarkToTrack,
                config.tracerFactory,
            )
        }
    }

    private fun processTraceAnnotations(
        file: File,
        mark: TraceAnnotationMark,
        processed: TraceAnnotationMark,
        tracerFactortStr: String,
    ): Int {
        val tag = TraceProcessingParams.TAG
        var text = file.readText()
        val codeGenerator = createCodeGenerator(mark.language)
        val methodExtractor = TextExtractor(
            patternProducer = createPatternProducerForTracedMethods(mark),
            patternSearcher = createPatternSearcherForTracedMethods(mark),
            resultSorter = MethodsSorter(text),
        )
        val tracedMethods = methodExtractor.extract(text)
        if (tracedMethods.isEmpty()) {
            println("no traced methods.")
            return 0
        }
        var indexOfTraceInFile = 0
        println("processing trace for file " + file.name)
        for (method in tracedMethods) {
            println("processing method ${method.name} pattern ${method.patternType} ${mark.shortVersion} ")
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
                val paramsStr = paramsExtractor
                    .extract(method.paramBlock)
                    .joinToString(", ") { it.name }

                val newLine = (method.wholeSignature + "")
                    .replace(mark.shortVersion, processed.longVersion)
                    .replace(mark.longVersion, processed.longVersion)

                val alto = getMethodAlterationOffset(method, file)
                println("alterationOffset = $alto + $indexOfTraceInFile for file ${file.name} and method ${method.name}")
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
                println("skipping method ${method.name}...")
            }
        }
        file.writeText(text)
        println("processing trace done for file " + file.name)
        return indexOfTraceInFile
    }

    fun setMethodAlterationOffset(method: TracedMethod, file: File, offset: Int) {
        val key = method.wholeSignature + file.name
        alterationsMap[key] = offset
    }

    fun getMethodAlterationOffset(method: TracedMethod, file: File): Int {
        val key = method.wholeSignature + file.name
        return alterationsMap[key] ?: 0
    }

    companion object {
        var alterationsMap = mutableMapOf<String, Int>()
    }
}
