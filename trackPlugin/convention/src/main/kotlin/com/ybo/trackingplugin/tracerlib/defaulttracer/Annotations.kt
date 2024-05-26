package com.ybo.trackingplugin.tracerlib.defaulttracer

import com.ybo.trackingplugin.tracerlib.defaulttracer.tracers.Returner

annotation class DefTraceTest()

annotation class ReverseTrace(val target: String)

/**
 * annotate this to your implementation of a [Returner], to be able to
 * trace return values
 */
annotation class ReturnTrace()
