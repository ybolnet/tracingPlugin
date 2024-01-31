package com.ybo.trackingplugin.tasks.utils

import com.ybo.trackingplugin.tasks.data.PatternToSearch

interface PatternProducer {

    fun produce(): List<PatternToSearch>
}
