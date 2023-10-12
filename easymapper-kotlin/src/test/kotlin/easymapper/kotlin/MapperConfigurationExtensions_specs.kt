package easymapper.kotlin

import easymapper.Mapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MapperConfigurationExtensions_specs {

    class User(val id: Long, val username: String, val passwordHash: String)

    class UserView(val id: Long, val username: String)

    data class Pricing(val listPrice: Double, val discount: Double) {

        fun calculateSalePrice(): Double {
            return listPrice - discount
        }
    }

    data class PricingView(
        val listPrice: Double = 0.0,
        val discount: Double = 0.0,
        val salePrice: Double = 0.0
    )

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

    @Test
    fun `addMapping returns the same builder`() {
        Mapper { assertThat(it.addMapping<Pricing, PricingView> {}).isSameAs(it) }
    }

    @AutoParameterizedTest
    fun `addMapping correctly works`(source: Pricing) {
        val mapper = KotlinMapper { c -> c
            .addMapping<Pricing, PricingView> { m -> m
                .set(PricingView::salePrice.name) { it.calculateSalePrice() }
            }
        }

        val actual: PricingView = mapper.map(source, PricingView::class.java)

        assertThat(actual.salePrice).isEqualTo(source.calculateSalePrice())
    }

    @Test
    fun `set returns the same builder`() {
        Mapper { c ->
            c.addMapping<Pricing, PricingView> { m ->
                assertThat(m.set(PricingView::salePrice) { it.calculateSalePrice() })
                    .isSameAs(m)
            }
        }
    }

    @AutoParameterizedTest
    fun `set correctly works`(source: Pricing) {
        val mapper = KotlinMapper { c -> c
            .addMapping<Pricing, PricingView> { m -> m
                .set(PricingView::salePrice) { it.calculateSalePrice() }
            }
        }

        val actual: PricingView = mapper.map(source, PricingView::class.java)

        assertThat(actual.salePrice).isEqualTo(source.calculateSalePrice())
    }

    @Test
    fun `addTransform returns the same builder`() {
        Mapper { c ->
            assertThat(c.addTransform<Pricing, PricingView> {
                PricingView(
                    it.listPrice,
                    it.discount,
                    it.calculateSalePrice(),
                )
            }).isSameAs(c)
        }
    }

    @AutoParameterizedTest
    fun `addTransform correctly works`(source: Pricing) {
        val mapper = KotlinMapper { c ->
            c.addTransform<Pricing, PricingView> {
                PricingView(
                    it.listPrice,
                    it.discount,
                    it.calculateSalePrice(),
                )
            }
        }

        val actual: PricingView = mapper.map(source, PricingView::class.java)

        assertThat(actual.salePrice).isEqualTo(source.calculateSalePrice())
    }
}
