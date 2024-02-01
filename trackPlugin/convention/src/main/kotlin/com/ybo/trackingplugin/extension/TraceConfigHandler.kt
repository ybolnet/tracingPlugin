package com.ybo.trackingplugin.extension

import org.gradle.api.GradleException

open class TraceProcessConfigHandler {

    val editableListOfConfigs: MutableList<TraceConfig> = mutableListOf()
    internal val configs: List<TraceConfig> get() = editableListOfConfigs.toList()

    fun add(init: TraceConfig.() -> Unit) {
        val config = TraceConfig()
        config.init()
        config.check()
        config.checkInsertability(editableListOfConfigs)
        editableListOfConfigs.add(config)
    }

    private fun TraceConfig.checkInsertability(configs: List<TraceConfig>) {
        if (!configs.fold(true) { acc, candidate ->
                acc && candidate.compatible(this)
            }
        ) {
            throw GradleException("cannot add config $name because it clashes with another config")
        }
    }

    fun TraceConfig.compatible(anotherConfig: TraceConfig): Boolean {
        val toProcessAnnotationDifferent = (
            toBeProcessedAnnotation?.lowercase()
                ?: ""
            ) != (anotherConfig.toBeProcessedAnnotation?.lowercase() ?: "")
        val processedAnnotationDifferent = (
            alreadyProcessedAnnotation?.lowercase()
                ?: ""
            ) != (anotherConfig.alreadyProcessedAnnotation?.lowercase() ?: "")
        val nameDifferent = name.lowercase() != anotherConfig.name
        return toProcessAnnotationDifferent && processedAnnotationDifferent && nameDifferent
    }

    private fun TraceConfig.check() {
        if (name.isEmpty()) {
            throw GradleException("in trace config, name must be defined")
        }
        if (toBeProcessedAnnotation == null) {
            throw GradleException("in trace config, toBeProcessedAnnotation must be defined")
        }
        if (alreadyProcessedAnnotation == null) {
            throw GradleException("in trace config, alreadyProcessedAnnotation must be defined")
        }
        if (tracerFactory.isEmpty()) {
            throw GradleException("in trace config, tracerFactory must be defined")
        }
        if (srcPath == null) {
            throw GradleException("in trace config, srcPath must be defined")
        }
        if (!toBeProcessedAnnotation!!.contains('.') || !alreadyProcessedAnnotation!!.contains('.')) {
            throw GradleException("in trace config, annotations must be defined in their full form (f.i. pretty.nice.package.TraceAnnotation)")
        }
        if (toBeProcessedAnnotation.equals(alreadyProcessedAnnotation)) {
            throw GradleException("in trace config, toBeProcessedAnnotation and alreadyProcessedAnnotation must be different")
        }
    }
}
