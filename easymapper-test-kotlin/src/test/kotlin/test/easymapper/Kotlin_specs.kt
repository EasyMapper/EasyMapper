package test.easymapper

import autoparams.AutoSource
import easymapper.Mapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import java.beans.ConstructorProperties

internal class Kotlin_specs {

    class User
    @ConstructorProperties(value = ["id", "username", "passwordHash"])
    constructor(
        val id: Long,
        val username: String,
        val passwordHash: String)

    @ParameterizedTest
    @AutoSource
    internal fun `sut correctly maps object with @ConstructorProperties annotation`(
        sut: Mapper,
        source: User
    ) {
        val actual: User = sut.map(source, User::class.java, User::class.java)
        assertThat(actual).usingRecursiveComparison().isEqualTo(source)
    }
}
