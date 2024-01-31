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
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

open class ProcessTraceTask : BrowsingTask() {

    @Input
    var tracerFactory: String = ""

    @TaskAction
    fun processTrace() {
        if (pathForSourceCode == null) {
            throw GradleException("srcPath must be defined")
        }
        browseCode {
            processTraceAnnotations(
                it.file,
                it.toBeProcessedMarkToTrack,
                it.alreadyProcessedMarkToTrack,
            )
        }
    }

    private fun processTraceAnnotations(
        file: File,
        mark: TraceAnnotationMark,
        processed: TraceAnnotationMark,
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

                //extractParamsString(method, mark.language)
                var newLine = (method.wholeMethod + "")
                    .replace(mark.shortVersion, processed.longVersion)
                    .replace(mark.longVersion, processed.longVersion)
                /*if (mark.language == KOTLIN) {
                    if (!method.wholeMethod.contains("{")) {
                        throw GradleException("problem of missing '{'")
                    }
                    newLine = newLine.replaceLastOccurrence('{', '=')
                }*/
                text = text.replace(
                    method.wholeMethod,
                    newLine + codeGenerator.generate(
                        params = paramsStr,
                        tracerFactoryString = tracerFactory,
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

    private fun extractParamsString(method: TracedMethod, language: TracedLanguage): String {
        val tracedMethodParams = extractTracedParams(method, language)
        return tracedMethodParams.joinToString(", ") { it.name }
    }

    private fun extractTracedMethods(
        text: String,
        toBeProcessedMark: TraceAnnotationMark,
    ): List<TracedMethod> {
        println("extracting method in here. ${toBeProcessedMark.language} is the language")
        val toProcessAnnotationShort = toBeProcessedMark.shortVersion
        val toProcessAnnotationLong = toBeProcessedMark.longVersion

        val catchingMethodsRegex = when (toBeProcessedMark.language) {
            KOTLIN -> Regex("($toProcessAnnotationShort|$toProcessAnnotationLong)\\s*\\n*(?:\\s*@[^\\n]*\\s*\\n*)*\\s*(?:override)?\\s+fun\\b\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*(?::\\s*\\w+)?\\s*\\{[\\t ]*\\n(\\s*)")
            JAVA -> Regex("($toProcessAnnotationShort|$toProcessAnnotationLong)\\s*\\n*(?:\\s*@[^\\n]*\\s*\\n*)*\\s*(?:public\\s+|protected\\s+|private\\s+|static\\s+|\\s)[\\w<>\\[\\]\\.]+\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*\\{[ \\t]*\\n*([ \\t]*)")
            else -> null
        }
        println("regex $catchingMethodsRegex")

        val matcher = catchingMethodsRegex?.findAll(text)
        if (matcher == null || matcher.count() == 0) {
            println("nothing interesting here")
            return emptyList()
        }
        return matcher.map {
            TracedMethod(
                wholeMethod = it.groupValues[0],
                indentationInsideMethod = if (it.groupValues.size > 4) it.groupValues[4] else " ",
                paramBlock = it.groupValues[3],
                methodName = it.groupValues[2],
            )
        }.toList()
    }

    private fun extractTracedParams(
        method: TracedMethod,
        language: TracedLanguage,
    ): List<TracedMethodParam> {
        println("extracting params... language $language")
        val extractingParamsRegex = when (language) {
            KOTLIN -> Regex("\\b(\\w+)\\s*:\\s*([^,=]+(?:\\s*=\\s*[^,]+)?)")
            JAVA -> Regex("(?:@(?:\\w+(?:\\n*\\.\\n*)?)+(?:\\(.*\\))*\\s*)?(?:final\\s)?[\\w.]+\\s+(\\w+)\\s*,?")
            else -> null
        }
        println("regex for params $extractingParamsRegex")
        val matcherParams = extractingParamsRegex?.findAll(method.paramBlock)
        if (matcherParams == null || matcherParams.count() == 0) {
            if (method.paramBlock.isAllWhitespace()) {
                println(
                    "could not extract any params from :\n " +
                        "${method.wholeMethod}\n" +
                        " because there are none, and it's ok.",
                )
            } else {
                println(
                    "\"extracted param block is ill-formatted and it is not normal:\\n\" +\n" +
                        "                        \"method : ${method.wholeMethod}\\n\" +\n" +
                        "                        \"param block ${method.paramBlock}\\n\", ",
                )
                /*throw GradleException(
                    "extracted param block is ill-formatted and it is not normal:\n" +
                        "method : ${method.wholeMethod}\n" +
                        "param block ${method.paramBlock}\n",
                )*/
            }
            return emptyList()
        }
        return matcherParams.map {
            TracedMethodParam(it.groupValues[1])
        }.toList()
    }

    fun String.replaceLastOccurrence(charToReplace: Char, newChar: Char): String {
        val lastIndexOfChar = lastIndexOf(charToReplace)

        return if (lastIndexOfChar >= 0) {
            substring(0, lastIndexOfChar) + newChar + substring(lastIndexOfChar + 1)
        } else {
            this // Character not found, return the original string
        }
    }
}
