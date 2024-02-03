package com.ybo.trackingplugin

import java.util.Base64

fun String.toB64(): String {
    val bytes = this.toByteArray()
    val encodedBytes = Base64.getEncoder().encode(bytes)
    return String(encodedBytes)
}

fun String.decodedFromB64(): String {
    val decodedBytes = Base64.getDecoder().decode(this)
    return String(decodedBytes)
}
