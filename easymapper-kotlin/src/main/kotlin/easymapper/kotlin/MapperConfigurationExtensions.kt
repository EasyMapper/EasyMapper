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

inline fun <reified S, reified D> MapperConfiguration.addTransform(
    function: Function<S, D>,
): MapperConfiguration {
    return this.addTransform(S::class.java, D::class.java, function)
}

inline fun <reified S, reified D> MapperConfiguration.addMapping(
    configurer: Consumer<MappingBuilder<S, D>>,
): MapperConfiguration {
    return this.addMapping(S::class.java, D::class.java, configurer)
}

fun <S, D> MappingBuilder<S, D>.set(
    property: KProperty<*>,
    function: Function<S, Any>,
): MappingBuilder<S, D> {
    return this.set(property.name, function)
}
