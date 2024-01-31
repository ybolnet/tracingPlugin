package com.ybo.trackingplugin.tasks.data

interface PatternToSearch {
    val name: String
    fun regex(): Regex
}
