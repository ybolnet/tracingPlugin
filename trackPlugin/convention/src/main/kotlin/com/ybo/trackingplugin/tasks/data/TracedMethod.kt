package com.ybo.trackingplugin.tasks.data

data class TracedMethod(
    val wholeSignature: String,
    val indentationInsideMethod: String,
    val paramBlock: String,
    val name: String = "",
)