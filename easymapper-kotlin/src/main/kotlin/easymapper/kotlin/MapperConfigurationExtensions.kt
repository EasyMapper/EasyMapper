package easymapper.kotlin

import easymapper.CompositeParameterNameResolver
import easymapper.MapperConfigurationBuilder

fun MapperConfigurationBuilder.useKotlin(): MapperConfigurationBuilder {
    this.constructorExtractor = KotlinConstructorExtractor()

    this.parameterNameResolver = CompositeParameterNameResolver(
        KotlinParameterNameResolver(),
        this.parameterNameResolver,
    )

    return this
}
