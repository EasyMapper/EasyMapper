package easymapper.kotlin

import easymapper.Mapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MapperConfigurationExtensions_specs {

    @Test
    fun `useKotlin returns the same builder`() {
        Mapper { config ->
            assertThat(config.useKotlin()).isSameAs(config)
        }
    }

    @AutoParameterizedTest
    fun `useKotlin makes mapper map Kotlin classes`(source: User) {
        val mapper = Mapper { config -> config.useKotlin() }
        val actual: UserView = mapper.map(source, UserView::class.java)
        assertThat(actual).usingRecursiveComparison().isEqualTo(source)
    }

    @AutoParameterizedTest
    fun `useKotlin makes mapper map data classes`(source: Pricing) {
        val mapper = Mapper { config -> config.useKotlin() }
        val actual: Pricing = mapper.map(source, Pricing::class.java)
        assertThat(actual).usingRecursiveComparison().isEqualTo(source)
    }

    @AutoParameterizedTest
    fun `useKotlin makes mapper map data classes with default arguments`(source: PricingView) {
        val mapper = Mapper { config -> config.useKotlin() }
        val actual: PricingView = mapper.map(source, PricingView::class.java)
        assertThat(actual).usingRecursiveComparison().isEqualTo(source)
    }
}
