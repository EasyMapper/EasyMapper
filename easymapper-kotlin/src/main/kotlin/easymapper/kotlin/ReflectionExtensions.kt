package easymapper.kotlin

import java.lang.reflect.Constructor
import java.lang.reflect.Parameter
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.kotlinFunction

internal val Parameter.index: Int
    get() = this.declaringExecutable.parameters.indexOf(this)

internal val Parameter.kotlinParameter: KParameter?
    get() = when (val executable = this.declaringExecutable) {
        is Constructor<*> -> executable.kotlinFunction?.parameters?.get(this.index)
        else -> null
    }
