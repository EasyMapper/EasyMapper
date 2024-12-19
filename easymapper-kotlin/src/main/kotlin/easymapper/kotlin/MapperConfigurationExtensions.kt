package easymapper.kotlin

import easymapper.CompositeParameterNameResolver
import easymapper.MapperConfiguration
import easymapper.MappingBuilder
import java.util.function.Consumer
import java.util.function.Function
import kotlin.reflect.KProperty

fun MapperConfiguration.useKotlin(): MapperConfiguration {
    this.constructorExtractor = KotlinConstructorExtractor()

    this.parameterNameResolver = CompositeParameterNameResolver(
        KotlinParameterNameResolver(),
        this.parameterNameResolver,
    )

    return this
}

inline fun <reified S, reified T> MapperConfiguration.addTransform(
    function: Function<S, T>,
): MapperConfiguration {
    return this.addTransform(S::class.java, T::class.java, function)
}

inline fun <reified S, reified T> MapperConfiguration.addMapping(
    configurer: Consumer<MappingBuilder<S, T>>,
): MapperConfiguration {
    return this.addMapping(S::class.java, T::class.java, configurer)
}

fun <S, T> MappingBuilder<S, T>.set(
    property: KProperty<*>,
    function: Function<S, Any>,
): MappingBuilder<S, T> {
    return this.set(property.name, function)
}
