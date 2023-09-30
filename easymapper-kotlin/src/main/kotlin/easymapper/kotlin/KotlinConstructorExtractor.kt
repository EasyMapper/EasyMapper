package easymapper.kotlin

import easymapper.ConstructorExtractor
import java.lang.reflect.Constructor
import kotlin.reflect.jvm.kotlinFunction

internal class KotlinConstructorExtractor : ConstructorExtractor {

    override fun extract(type: Class<*>): MutableCollection<Constructor<*>> {
        return type
            .constructors
            .filter { it.kotlinFunction != null }
            .toMutableList()
    }
}
