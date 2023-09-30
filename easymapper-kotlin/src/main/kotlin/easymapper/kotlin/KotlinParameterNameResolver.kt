package easymapper.kotlin

import easymapper.ParameterNameResolver
import java.lang.reflect.Parameter
import java.util.Optional

internal class KotlinParameterNameResolver : ParameterNameResolver {

    override fun tryResolveName(parameter: Parameter): Optional<String> {
        return Optional.ofNullable(parameter.kotlinParameter?.name)
    }
}
