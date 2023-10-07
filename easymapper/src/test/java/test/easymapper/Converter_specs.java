package test.easymapper;

import easymapper.ConversionContext;
import easymapper.Mapper;
import org.junit.jupiter.api.Test;
import test.easymapper.fixture.MutableBag;
import test.easymapper.fixture.Pricing;
import test.easymapper.fixture.PricingView;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.function.Function.identity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class Converter_specs {

    @Test
    void addConverter_is_fluent() {
        BiFunction<Integer, ConversionContext, Integer> function = (source, context) -> source;
        new Mapper(c -> assertThat(
            c.addConverter(int.class, int.class, function)).isSameAs(c));
    }

    @Test
    void addConverter_has_guard_against_null_source_type() {
        BiFunction<Integer, ConversionContext, Integer> function = (source, context) -> source;
        assertThatThrownBy(() ->
            new Mapper(c -> c.addConverter(null, int.class, function)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addConverter_has_guard_against_null_destination_type() {
        BiFunction<Integer, ConversionContext, Integer> function = (source, context) -> source;
        assertThatThrownBy(() ->
            new Mapper(c -> c.addConverter(int.class, null, function)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addConverter_has_guard_against_null_function() {
        BiFunction<Integer, ConversionContext, Integer> function = null;
        assertThatThrownBy(() ->
            new Mapper(c -> c.addConverter(int.class, int.class, function)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void addConverter_correctly_provides_context(PricingView source) {
        // Arrange
        MutableBag<ConversionContext> bag = new MutableBag<>();

        Mapper sut = new Mapper(config -> config
            .addConverter(PricingView.class, Pricing.class, (pricing, context) -> {
                bag.setValue(context);
                return new Pricing(pricing.getListPrice(), pricing.getDiscount());
            })
        );

        // Act
        sut.map(source, PricingView.class, Pricing.class);

        // Assert
        ConversionContext actual = bag.getValue();
        assertThat(actual).isNotNull();
        assertThat(actual.getMapper()).isSameAs(sut);
        assertThat(actual.getSourceType()).isSameAs(PricingView.class);
        assertThat(actual.getDestinationType()).isSameAs(Pricing.class);
    }

    @AutoParameterizedTest
    void addConverter_correctly_adds_converter(Pricing source) {
        // Arrange
        Mapper sut = new Mapper(config -> config
            .addConverter(
                Pricing.class,
                PricingView.class,
                (pricing, context) -> new PricingView(
                    pricing.getListPrice(),
                    pricing.getDiscount(),
                    pricing.getListPrice() - pricing.getDiscount())));

        // Act
        PricingView actual = sut.map(source, Pricing.class, PricingView.class);

        // Assert
        assertThat(actual.getSalePrice())
            .isEqualTo(source.getListPrice() - source.getDiscount());
    }

    @AutoParameterizedTest
    void addConverter_overrides_existing_converter(String source) {
        Mapper sut = new Mapper(config -> config
            .addConverter(String.class, String.class, x -> x + "1")
            .addConverter(String.class, String.class, x -> x + "2"));

        String actual = sut.map(source, String.class, String.class);

        assertThat(actual).isEqualTo(source + "2");
    }

    @Test
    void light_addConverter_is_fluent() {
        new Mapper(c -> assertThat(
            c.addConverter(int.class, int.class, identity())).isSameAs(c));
    }

    @Test
    void light_addConverter_has_guard_against_null_function() {
        Function<Integer, Integer> function = null;
        assertThatThrownBy(() ->
            new Mapper(c -> c.addConverter(int.class, int.class, function)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void light_addConverter_correctly_adds_converter(Pricing source) {
        // Arrange
        Mapper sut = new Mapper(config -> config
            .addConverter(
                Pricing.class,
                PricingView.class,
                pricing -> new PricingView(
                    pricing.getListPrice(),
                    pricing.getDiscount(),
                    pricing.getListPrice() - pricing.getDiscount())));

        // Act
        PricingView actual = sut.map(source, Pricing.class, PricingView.class);

        // Assert
        assertThat(actual.getSalePrice())
            .isEqualTo(source.getListPrice() - source.getDiscount());
    }
}
