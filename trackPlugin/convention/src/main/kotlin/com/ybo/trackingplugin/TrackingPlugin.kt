package com.ybo.trackingplugin

import com.ybo.trackingplugin.tasks.ProcessTraceTask
import com.ybo.trackingplugin.tasks.TraceProcessingParams
import com.ybo.trackingplugin.tasks.UnprocessTraceTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.register

open class TrackingPluginExtension {
    var trackables: Array<String> = emptyArray()
    var tracerFactory: String = ""
    var exclude: Array<String>? = null
    var toBeProcessedAnnotation: String? = null
    var alreadyProcessedAnnotation: String? = null
    var srcPath: String? = null
}

class TrackingPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            // Create an extension for the plugin
            val trackingConfig =
                project.extensions.create(TraceProcessingParams.TRACING_FEATURE_CONFIG, TrackingPluginExtension::class.java)

            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            tasks.register<ProcessTraceTask>(TraceProcessingParams.PROCESSING_TASK_NAME) {
                trackingConfig.checked().let {
                    traceAnnotation = it.toBeProcessedAnnotation
                    processedAnnotation = it.alreadyProcessedAnnotation
                    pathForSourceCode = it.srcPath
                    tracerFactory = it.tracerFactory
                    excluding = it.exclude ?: emptyArray()
                }
                doLast {
                    println("PROCESSTRACE processing trace")
                }
            }

            tasks.register<UnprocessTraceTask>(TraceProcessingParams.REVERSE_PROCESSING_TASK_NAME) {
                trackingConfig.checked().let {
                    traceAnnotation = it.toBeProcessedAnnotation
                    processedAnnotation = it.alreadyProcessedAnnotation
                    pathForSourceCode = it.srcPath
                    excluding = it.exclude ?: emptyArray()
                }
                doLast {
                    println("PROCESSTRACE Put trace annotation back to normal")
                }
            }

            afterEvaluate {
                trackingConfig.checked().trackables.forEach { taskName ->
                    tasks.getByName(taskName).let {
                        tasks.register("${TraceProcessingParams.TRACABLE_TASKS_PREFIX}${it.name.capitalized()}") {
                            group = TraceProcessingParams.TASK_PROCESSING_GROUP
                            // Hook prepareBuild to execute before the assemble task
                            dependsOn(TraceProcessingParams.PROCESSING_TASK_NAME)

                            // Hook trackable to execute after prepareBuild
                            dependsOn(it.name)

                            // Hook postpareBuild to execute after the trackable task
                            finalizedBy(TraceProcessingParams.REVERSE_PROCESSING_TASK_NAME)
                            it.finalizedBy(TraceProcessingParams.REVERSE_PROCESSING_TASK_NAME)
                        }
                    }
                }
            }
        }
    }

    fun TrackingPluginExtension.checked(): TrackingPluginExtension {
        if (toBeProcessedAnnotation == null) {
            throw GradleException("toBeProcessedAnnotation must be defined")
        }
        if (alreadyProcessedAnnotation == null) {
            throw GradleException("alreadyProcessedAnnotation must be defined")
        }
        if (tracerFactory.isEmpty()) {
            throw GradleException("tracerFactory must be defined")
        }
        if (srcPath == null) {
            throw GradleException("srcPath must be defined")
        }
        if (!toBeProcessedAnnotation!!.contains('.') || !alreadyProcessedAnnotation!!.contains('.')) {
            throw GradleException("annotations must be defined in their full form (f.i. pretty.nice.package.TraceAnnotation)")
        }
        if (toBeProcessedAnnotation.equals(alreadyProcessedAnnotation)) {
            throw GradleException("toBeProcessedAnnotation and alreadyProcessedAnnotation must be different")
        }
        return this
    }
}
