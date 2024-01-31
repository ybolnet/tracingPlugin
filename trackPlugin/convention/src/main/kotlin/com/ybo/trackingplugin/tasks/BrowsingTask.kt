package com.ybo.trackingplugin.tasks

import com.ybo.trackingplugin.tasks.data.TraceAnnotationMark
import com.ybo.trackingplugin.tasks.data.getLanguage
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Input
import java.io.File

open class BrowsingTask : DefaultTask() {

    init {
        group = TraceProcessingParams.TASK_PROCESSING_GROUP
    }

    @Input
    var traceAnnotation: String? = null

    @Input
    var processedAnnotation: String? = null

    @Input
    var pathForSourceCode: String? = null

    @Input
    var excluding: Array<String> = emptyArray()

    data class TrackedFile(
        val file: File,
        val toBeProcessedMarkToTrack: TraceAnnotationMark,
        val alreadyProcessedMarkToTrack: TraceAnnotationMark,
    )

    fun browseCode(block: (file: TrackedFile) -> Unit) {
        println("looking for files in $pathForSourceCode ")
        val fileTree: FileTree =
            project.fileTree(pathForSourceCode + "")
                .matching {
                    include("**/*.kt")
                    include("**/*.java")
                    if(excluding.isNotEmpty()){
                        exclude(*excluding)
                    }

                }
        println("tree ${fileTree.files} ")

        fileTree.forEach {
            println("dealing with file ${it.name}")
            if (it.canRead()) {
                println("we can process ${it.name}")
                val processedMarkToTrack =
                    TraceAnnotationMark(processedAnnotation, it.getLanguage())
                val toBeProcessedMarkToTrack =
                    TraceAnnotationMark(traceAnnotation, it.getLanguage())
                block(
                    TrackedFile(
                        file = it,
                        toBeProcessedMarkToTrack = toBeProcessedMarkToTrack,
                        alreadyProcessedMarkToTrack = processedMarkToTrack,
                    ),
                )
            } else {
                println("we cannot read ${it.name}")
            }
        }
    }
}
