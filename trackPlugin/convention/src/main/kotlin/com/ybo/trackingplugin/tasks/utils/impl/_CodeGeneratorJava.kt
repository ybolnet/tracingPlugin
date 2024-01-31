package com.ybo.trackingplugin.tasks.utils.impl

import com.ybo.trackingplugin.tasks.utils.CodeGenerator

class _CodeGeneratorJava : CodeGenerator() {
    override fun generate(
        params: String,
        tracerFactoryString: String,
        insideMethodIndentation: String,
        methodName: String,
        tag: String,
    ): String {
        return "/*$tag*/ $tracePerformerPackage.INSTANCE.trace(" +
            "new " + tracerFactoryString + "(), " +
            "\"" + methodName + "\", " +
            "true, " +
            "new Object[]{" + params + "} " +
            ");\n" +
            insideMethodIndentation
    }
}
