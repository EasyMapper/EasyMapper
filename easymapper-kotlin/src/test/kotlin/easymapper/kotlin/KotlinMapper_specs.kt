package easymapper.kotlin

import easymapper.Mapper
import org.assertj.core.api.Assertions.assertThat

class KotlinMapper_specs {

    @AutoParameterizedTest
    fun `sut inherits from Mapper`(sut: KotlinMapper) {
        assertThat(sut).isInstanceOf(Mapper::class.java)
    }

    @AutoParameterizedTest
    fun `sut maps data classes`(source: Pricing) {
        val sut = KotlinMapper()
        val actual: Pricing = sut.map(source, Pricing::class.java)
        assertThat(actual).usingRecursiveComparison().isEqualTo(source)
    }

    @AutoParameterizedTest
    fun `sut maps data classes with default arguments`(
        sut: KotlinMapper,
        source: PricingView
    ) {
        val actual: PricingView = sut.map(source, PricingView::class.java)
        assertThat(actual).usingRecursiveComparison().isEqualTo(source)
    }

    @AutoParameterizedTest
    fun `sut correctly applies configuration`(source: Pricing) {
        val sut = KotlinMapper { c -> c
            .addMapping(Pricing::class.java, PricingView::class.java) {
                m -> m.set(PricingView::salePrice.name) { it.listPrice - it.discount }
            }
        }

        val actual: PricingView = sut.map(source, PricingView::class.java)

        assertThat(actual.salePrice).isEqualTo(source.listPrice - source.discount)
    }
}
