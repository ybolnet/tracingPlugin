package com.ybo.trackingplugin.extension

import com.ybo.trackingplugin.tracerlib.defaulttracer.DefTraceTest
import com.ybo.trackingplugin.tracerlib.defaulttracer.DefUnTraceTest
import com.ybo.trackingplugin.tracerlib.defaulttracer.ReturnTrace
import com.ybo.trackingplugin.tracerlib.defaulttracer.ReturnUntrace
import com.ybo.trackingplugin.tracerlib.defaulttracer.tracers.TraceDefaultFactory
import org.gradle.api.GradleException

open class TraceProcessConfigHandler {

    private val editableListOfConfigs: MutableList<TraceConfig> = mutableListOf()
    internal val configs: List<TraceConfig> get() = editableListOfConfigs.toList()

    /** adds a configuration, ie a set of annotation represening what method to trace,
     * and a factory to create a tracer for these traced methods.*/
    fun add(init: TraceConfig.() -> Unit) {
        val config = TraceConfig()
        config.init()
        config.check()
        config.checkInsertability(editableListOfConfigs)
        editableListOfConfigs.add(config)
    }

    /** adds a configuration,
     * but that is already prepopulated with default @DefTraceTest annotations*/
    fun addDefaultConfig(init: DefaultTraceConfig.() -> Unit) {
        val config = TraceConfig()
        val defaultTraceConfig = DefaultTraceConfig()
        defaultTraceConfig.init()
        config.apply {
            name = "DefaultEmbeddedConfig"
            tracerFactory = TraceDefaultFactory::class.java.canonicalName
            toBeProcessedAnnotation = DefTraceTest::class.java.canonicalName
            alreadyProcessedAnnotation = DefUnTraceTest::class.java.canonicalName
            exclude = defaultTraceConfig.exclude
            srcPath = defaultTraceConfig.srcPath
        }
        config.check()
        config.checkInsertability(editableListOfConfigs)
        editableListOfConfigs.add(config)
    }

    /** adds a configuration,
     * but that is already prepopulated for return value feature*/
    fun addReturnValueConfig(init: ReturnTraceConfig.() -> Unit) {
        val config = TraceConfig()
        val returnConfig = ReturnTraceConfig()
        returnConfig.init()
        config.apply {
            name = "ReturnTraceConfig"
            toBeProcessedAnnotation = ReturnTrace::class.java.canonicalName
            alreadyProcessedAnnotation = ReturnUntrace::class.java.canonicalName
            tracerFactory = returnConfig.tracerFactory
            exclude = returnConfig.exclude
            srcPath = returnConfig.srcPath
        }

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
            throw GradleException("in trace config $name, toBeProcessedAnnotation must be defined")
        }
        if (alreadyProcessedAnnotation == null) {
            throw GradleException("in trace config $name, alreadyProcessedAnnotation must be defined")
        }
        if (tracerFactory.isEmpty()) {
            throw GradleException("in trace config $name, tracerFactory must be defined")
        }
        if (srcPath == null) {
            throw GradleException("in trace config $name, srcPath must be defined")
        }
        if (!toBeProcessedAnnotation!!.contains('.') || !alreadyProcessedAnnotation!!.contains('.')) {
            throw GradleException("in trace config $name, annotations must be defined in their full form (f.i. pretty.nice.package.TraceAnnotation)")
        }
        if (toBeProcessedAnnotation.equals(alreadyProcessedAnnotation)) {
            throw GradleException("in trace config $name, toBeProcessedAnnotation and alreadyProcessedAnnotation must be different")
        }
    }
}
