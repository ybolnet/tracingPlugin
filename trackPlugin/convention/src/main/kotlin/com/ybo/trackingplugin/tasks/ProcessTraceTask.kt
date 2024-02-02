package com.ybo.trackingplugin.tasks

import com.ybo.trackingplugin.tasks.data.TraceAnnotationMark
import com.ybo.trackingplugin.tasks.data.TracedLanguage
import com.ybo.trackingplugin.tasks.data.TracedLanguage.JAVA
import com.ybo.trackingplugin.tasks.data.TracedLanguage.KOTLIN
import com.ybo.trackingplugin.tasks.data.TracedMethod
import com.ybo.trackingplugin.tasks.data.TracedMethodParam
import com.ybo.trackingplugin.tasks.data.isAllWhitespace
import com.ybo.trackingplugin.tasks.utils.TextExtractor
import com.ybo.trackingplugin.tasks.utils.createCodeGenerator
import com.ybo.trackingplugin.tasks.utils.createPatternProducerForTracedMethods
import com.ybo.trackingplugin.tasks.utils.createPatternProducerForTracedParams
import com.ybo.trackingplugin.tasks.utils.createPatternSearcherForTracedMethods
import com.ybo.trackingplugin.tasks.utils.createPatternSearcherForTracedParams
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.File

open class ProcessTraceTask : BrowsingTask() {

    @TaskAction
    fun processTrace() {
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
    ): Boolean {
        val tag = TraceProcessingParams.TAG
        var text = file.readText()
        val codeGenerator = createCodeGenerator(mark.language)
        val methodExtractor = TextExtractor(
            patternProducer = createPatternProducerForTracedMethods(mark),
            patternSearcher = createPatternSearcherForTracedMethods(mark),
        )
        val paramsExtractor = TextExtractor(
            patternProducer = createPatternProducerForTracedParams(mark.language),
            patternSearcher = createPatternSearcherForTracedParams(mark.language),
        )

        val tracedMethods = methodExtractor.extract(text) // extractTracedMethods(text, mark)
        if (tracedMethods.isEmpty()) {
            return false
        }
        println("processing trace for file " + file.name)
        for (method in tracedMethods) {
            try {
                val paramsStr = paramsExtractor
                    .extract(method.paramBlock)
                    .joinToString(", ") { it.name }

                val newLine = (method.wholeMethod + "")
                    .replace(mark.shortVersion, processed.longVersion)
                    .replace(mark.longVersion, processed.longVersion)

                text = text.replace(
                    method.wholeMethod,
                    newLine + codeGenerator.generate(
                        params = paramsStr,
                        tracerFactoryString = tracerFactortStr,
                        insideMethodIndentation = method.indentationInsideMethod,
                        methodName = method.methodName,
                        tag = tag,
                    ),
                )
            } catch (error: GradleException) {
                error.printStackTrace()
                println("skipping this method...")
            }
        }
        file.writeText(text)
        println("processing trace done for file " + file.name)
        return true
    }
}
