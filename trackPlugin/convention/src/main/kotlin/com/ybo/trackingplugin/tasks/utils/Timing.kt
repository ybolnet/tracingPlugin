package com.ybo.trackingplugin.tasks.utils

class Timing {

    private val mapTimes = mutableMapOf<String, Long>()
    fun start(task: String) {
        //println("start $task")
        mapTimes[task] = System.currentTimeMillis()
    }

    fun end(task: String) {
        val time = System.currentTimeMillis() - (mapTimes[task] ?: System.currentTimeMillis())
        //println("task $task lasted $time")
    }
}
