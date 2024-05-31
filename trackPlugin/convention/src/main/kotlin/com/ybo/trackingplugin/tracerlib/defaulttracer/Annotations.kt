package com.ybo.trackingplugin.tracerlib.defaulttracer

import com.ybo.trackingplugin.tracerlib.defaulttracer.tracers.Returner

/**
 * default trace annotation that can be configured with
 * addDefaultConfig() in the tracing{} closure of build.gradle configuration
 */
annotation class DefTraceTest()

@Repeatable
annotation class ReverseTrace(val target: String)

/**
 * annotate this to your implementation of a [Returner], to be able to
 * trace return values
 */
annotation class ReturnTrace()
