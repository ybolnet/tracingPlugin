package com.ybo.trackingplugin.tasks.utils.impl.patterns

import com.ybo.trackingplugin.tasks.data.PatternToSearch

internal open class JavaParamsPattern : PatternToSearch<JavaParamPatternName> {
    override val name = JavaParamPatternName.JavaNormalParam
    override fun regex(): Regex {
        return Regex("(?:@(?:\\w+(?:\\n*\\.\\n*)?)+(?:\\(.*\\))*\\s*)?(?:final\\s)?[\\w.]+\\s+(\\w+)\\s*,?")
    }
}
