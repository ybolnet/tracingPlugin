package com.ybo.trackingplugin.tasks.utils.impl

import com.ybo.trackingplugin.tasks.utils.CodeGenerator
import com.ybo.trackingplugin.toB64

class _CodeGeneratorJava : CodeGenerator() {
    override fun generate(
        params: String,
        tracerFactoryString: String,
        insideMethodIndentation: String,
        methodName: String,
        tag: String,
        alterationOffset: Int,
    ): String {
        return "/*$tag*/ $tracePerformerPackage.INSTANCE.trace(" +
            "new " + tracerFactoryString + "(), " +
            "\"" + methodName.toB64() + "\", " +
            "true, " +
            "new Object[]{" + params + "}, " +
            alterationOffset +
            ");\n" +
            insideMethodIndentation
    }
}
