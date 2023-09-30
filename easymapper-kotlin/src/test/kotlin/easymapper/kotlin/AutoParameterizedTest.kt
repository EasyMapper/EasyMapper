package easymapper.kotlin

import autoparams.kotlin.AutoKotlinSource
import org.junit.jupiter.params.ParameterizedTest

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ParameterizedTest
@AutoKotlinSource
annotation class AutoParameterizedTest
