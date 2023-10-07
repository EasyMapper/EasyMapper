package test.easymapper;

import easymapper.ConversionContext;
import easymapper.ConverterFunction;
import easymapper.Mapper;
import easymapper.TypeReference;
import org.junit.jupiter.api.Test;
import test.easymapper.fixture.MutableBag;
import test.easymapper.fixture.Pricing;
import test.easymapper.fixture.PricingView;

import java.lang.reflect.Type;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class Converter_specs {

    @Test
    void addConverter_with_classes_is_fluent() {
        ConverterFunction<Integer, Integer> function = (source, context) -> source;
        new Mapper(c -> assertThat(
            c.addConverter(int.class, int.class, function)).isSameAs(c));
    }

    @Test
    void addConverter_with_classes_has_guard_against_null_source_type() {
        ConverterFunction<Integer, Integer> function = (source, context) -> source;
        assertThatThrownBy(() ->
            new Mapper(c -> c.addConverter(null, int.class, function)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addConverter_with_classes_has_guard_against_null_destination_type() {
        ConverterFunction<Integer, Integer> function = (source, context) -> source;
        assertThatThrownBy(() ->
            new Mapper(c -> c.addConverter(int.class, null, function)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addConverter_with_classes_has_guard_against_null_function() {
        ConverterFunction<Integer, Integer> function = null;
        assertThatThrownBy(() ->
            new Mapper(c -> c.addConverter(int.class, int.class, function)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void addConverter_with_classes_correctly_provides_context(PricingView source) {
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
    void addConverter_with_classes_correctly_adds_converter(Pricing source) {
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
    void addConverter_with_classes_overrides_existing_converter(String source) {
        Mapper sut = new Mapper(config -> config
            .addConverter(String.class, String.class, (s, c) -> s + "1")
            .addConverter(String.class, String.class, (s, c) -> s + "2"));

        String actual = sut.map(source, String.class, String.class);

        assertThat(actual).isEqualTo(source + "2");
    }

    @Test
    void addConverter_with_type_references_is_fluent() {
        new Mapper(c -> assertThat(
            c.addConverter(
                new TypeReference<Integer>() { },
                new TypeReference<Integer>() { },
                (source, context) -> source))
            .isSameAs(c));
    }

    @Test
    void addConverter_with_type_references_has_guard_against_null_source_type_reference() {
        TypeReference<Integer> sourceTypeReference = null;
        assertThatThrownBy(() ->
            new Mapper(c -> c.addConverter(
                sourceTypeReference,
                new TypeReference<Integer>() { },
                (source, context) -> source)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addConverter_with_type_references_has_guard_against_null_destination_type_reference() {
        TypeReference<Integer> destinationTypeReference = null;
        assertThatThrownBy(() ->
            new Mapper(c -> c.addConverter(
                new TypeReference<Integer>() { },
                destinationTypeReference,
                (source, context) -> source)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addConverter_with_type_references_has_guard_against_null_function() {
        ConverterFunction<Integer, Integer> function = null;
        assertThatThrownBy(() ->
            new Mapper(c -> c.addConverter(
                new TypeReference<Integer>() { },
                new TypeReference<Integer>() { },
                function)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void addConverter_with_type_references_correctly_adds_converter(Pricing source) {
        // Arrange
        Mapper sut = new Mapper(config -> config
            .addConverter(
                new TypeReference<Pricing>() { },
                new TypeReference<PricingView>() { },
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

    @Test
    void addConverter_with_predicates_is_fluent() {
        new Mapper(c -> assertThat(
            c.addConverter(
                type -> type.equals(int.class),
                type -> type.equals(int.class),
                (source, context) -> source))
            .isSameAs(c));
    }

    @Test
    void addConverter_with_predicates_has_guard_against_null_source_predicate() {
        ConverterFunction<Object, Object> function = (source, context) -> source;
        assertThatThrownBy(() ->
            new Mapper(c -> c.addConverter(null, type -> type.equals(int.class), function)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addConverter_with_predicates_has_guard_against_null_destination_predicate() {
        ConverterFunction<Object, Object> function = (source, context) -> source;
        assertThatThrownBy(() ->
            new Mapper(c -> c.addConverter(type -> type.equals(int.class), null, function)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addConverter_with_predicates_has_guard_against_null_function() {
        Function<Type, Boolean> predicate = type -> type.equals(int.class);
        ConverterFunction<Object, Object> function = null;
        assertThatThrownBy(() ->
            new Mapper(c -> c.addConverter(predicate, predicate, function)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void addConverter_with_predicates_correctly_add_converter(Pricing source) {
        // Arrange
        Mapper sut = new Mapper(config -> config
            .addConverter(
                type -> type.equals(Pricing.class),
                type -> type.equals(PricingView.class),
                (s, c) -> {
                    Pricing pricing = (Pricing) s;
                    return new PricingView(
                        pricing.getListPrice(),
                        pricing.getDiscount(),
                        pricing.getListPrice() - pricing.getDiscount());
                }));

        // Act
        PricingView actual = sut.map(source, Pricing.class, PricingView.class);

        // Assert
        assertThat(actual.getSalePrice())
            .isEqualTo(source.getListPrice() - source.getDiscount());
    }
}
