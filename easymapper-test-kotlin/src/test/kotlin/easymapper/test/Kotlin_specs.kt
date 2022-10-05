package easymapper.test

import autoparams.AutoSource
import easymapper.Mapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest

internal class Kotlin_specs {

    @ParameterizedTest
    @AutoSource
    internal fun `sut correctly maps object with @ConstructorProperties annotation`(
        sut: Mapper,
        source: User
    ) {
        val actual: User = sut.map(source, User::class.java)
        assertThat(actual).usingRecursiveComparison().isEqualTo(source)
    }
}
