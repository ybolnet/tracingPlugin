package com.ybo.trackingplugin.tasks.utils

import com.ybo.trackingplugin.tasks.data.PatternToSearch
import com.ybo.trackingplugin.tasks.utils.impl.patterns.PatternName

interface PatternProducer<out T : PatternName> {

    fun produce(): List<PatternToSearch<T>>
}
