package com.ybo.trackingplugin.tasks.utils

import com.ybo.trackingplugin.tasks.data.PatternToSearch

interface PatternSearcher<T> {

    fun search(text: String, patterns: List<PatternToSearch>): List<GroupOfResult<T>>

    data class GroupOfResult<T>(
        val patternName: String,
        val results: List<T>,
    )
}
