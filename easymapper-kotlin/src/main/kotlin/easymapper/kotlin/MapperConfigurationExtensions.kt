package easymapper.kotlin

import easymapper.CompositeParameterNameResolver
import easymapper.MapperConfiguration

fun MapperConfiguration.useKotlin(): MapperConfiguration {
    this.constructorExtractor = KotlinConstructorExtractor()

    this.parameterNameResolver = CompositeParameterNameResolver(
        KotlinParameterNameResolver(),
        this.parameterNameResolver,
    )

    return this
}
