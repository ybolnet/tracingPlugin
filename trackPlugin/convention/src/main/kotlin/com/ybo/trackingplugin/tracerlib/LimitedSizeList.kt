package com.ybo.trackingplugin.tracerlib

class LimitedSizeList<T>(private val maxSize: Int) : ArrayList<T>(maxSize) {
    override fun add(element: T): Boolean {
        super.add(0, element)
        if (size > maxSize) {
            removeAt(maxSize)
        }
        return true
    }
}