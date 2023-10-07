package test.easymapper;

import autoparams.Repeat;
import easymapper.ConstructorExtractor;
import easymapper.Mapper;
import easymapper.ParameterNameResolver;
import easymapper.ConversionContext;
import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import test.easymapper.fixture.HasBrokenConstructor;
import test.easymapper.fixture.ItemView;
import test.easymapper.fixture.MutableBag;
import test.easymapper.fixture.Order;
import test.easymapper.fixture.OrderView;
import test.easymapper.fixture.Price;
import test.easymapper.fixture.Pricing;
import test.easymapper.fixture.PricingView;
import test.easymapper.fixture.Recipient;
import test.easymapper.fixture.RecipientView;
import test.easymapper.fixture.User;
import test.easymapper.fixture.UserView;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparingInt;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MapperConfiguration_specs {

    @Test
    void sut_has_guard_against_null_source_type() {
        assertThatThrownBy(() ->
            new Mapper(config -> config
                .addMapping(null, OrderView.class, mapping -> { })));
    }

    @Test
    void sut_has_guard_against_null_destination_type() {
        assertThatThrownBy(() ->
            new Mapper(config -> config
                .addMapping(Order.class, null, mapping -> { })));
    }

    @Test
    void sut_has_guard_against_null_mapping_configurer() {
        assertThatThrownBy(() ->
            new Mapper(config -> config
                .addMapping(Order.class, OrderView.class, null)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void constructor_extractor_correctly_works(User source) {
        // Arrange
        ConstructorExtractor extractor = type -> stream(type.getConstructors())
            .sorted(comparingInt(Constructor::getParameterCount))
            .limit(1)
            .collect(toList());
        Mapper mapper = new Mapper(c -> c.setConstructorExtractor(extractor));

        // Act
        HasBrokenConstructor actual = mapper.map(
            source,
            User.class,
            HasBrokenConstructor.class);

        // Assert
        assertThat(actual.getId()).isEqualTo(source.getId());
        assertThat(actual.getUsername()).isEqualTo(HasBrokenConstructor.DEFAULT_USERNAME);
    }

    @AutoParameterizedTest
    void setConstructorExtractor_is_fluent() {
        new Mapper(c -> assertThat(
            c.setConstructorExtractor(t -> emptyList())).isSameAs(c));
    }

    @AutoParameterizedTest
    void setConstructorExtractor_has_guard_against_null_value() {
        assertThatThrownBy(() -> new Mapper(c -> c.setConstructorExtractor(null)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addMapping_is_fluent() {
        new Mapper(c -> assertThat(
            c.addMapping(Order.class, OrderView.class, m -> { })).isSameAs(c));
    }

    @Test
    void addTransform_is_fluent() {
        BiFunction<Integer, ConversionContext, Integer> function = (source, context) -> source;
        new Mapper(c -> assertThat(
            c.addTransform(int.class, int.class, function)).isSameAs(c));
    }

    @Test
    void addTransform_has_guard_against_null_source_type() {
        BiFunction<Integer, ConversionContext, Integer> function = (source, context) -> source;
        assertThatThrownBy(() ->
            new Mapper(c -> c.addTransform(null, int.class, function)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addTransform_has_guard_against_null_destination_type() {
        BiFunction<Integer, ConversionContext, Integer> function = (source, context) -> source;
        assertThatThrownBy(() ->
            new Mapper(c -> c.addTransform(int.class, null, function)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addTransform_has_guard_against_null_function() {
        BiFunction<Integer, ConversionContext, Integer> function = null;
        assertThatThrownBy(() ->
            new Mapper(c -> c.addTransform(int.class, int.class, function)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void addTransform_correctly_provides_context(PricingView source) {
        // Arrange
        MutableBag<ConversionContext> bag = new MutableBag<>();

        Mapper sut = new Mapper(config -> config
            .addTransform(PricingView.class, Pricing.class, (pricing, context) -> {
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
    void addTransform_correctly_adds_transform(Pricing source) {
        // Arrange
        Mapper sut = new Mapper(config -> config
            .addTransform(
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

    @Test
    void light_addTransform_is_fluent() {
        new Mapper(c -> assertThat(
            c.addTransform(int.class, int.class, identity())).isSameAs(c));
    }

    @Test
    void light_addTransform_has_guard_against_null_function() {
        Function<Integer, Integer> function = null;
        assertThatThrownBy(() ->
            new Mapper(c -> c.addTransform(int.class, int.class, function)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void light_addTransform_correctly_adds_transform(Pricing source) {
        // Arrange
        Mapper sut = new Mapper(config -> config
            .addTransform(
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

    @AutoParameterizedTest
    void set_is_fluent(User source) {
        new Mapper(config -> config.addMapping(
            User.class,
            UserView.class,
            mapping -> assertThat(mapping.set("id", User::getId)).isSameAs(mapping)));
    }

    @AutoParameterizedTest
    void set_correctly_configures_constructor_property_mapping(Pricing source) {
        // Arrange
        Mapper mapper = new Mapper(config -> config
            .addMapping(Pricing.class, PricingView.class, mapping -> mapping
                .set("salePrice", x -> x.getListPrice() - x.getDiscount())));

        // Act
        PricingView actual = mapper.map(
            source,
            Pricing.class,
            PricingView.class);

        // Assert
        assertThat(actual.getSalePrice())
            .isEqualTo(source.getListPrice() - source.getDiscount());
    }

    @AutoParameterizedTest
    void set_correctly_configures_settable_property_mapping(Recipient source) {
        // Arrange
        Mapper mapper = new Mapper(config -> config
            .addMapping(Recipient.class, RecipientView.class, mapping -> mapping
                .set("recipientName", Recipient::getName)
                .set("recipientPhoneNumber", Recipient::getPhoneNumber)));

        // Act
        RecipientView actual = mapper.map(
            source,
            Recipient.class,
            RecipientView.class);

        // Assert
        assertThat(actual.getRecipientName()).isEqualTo(source.getName());
        assertThat(actual.getRecipientPhoneNumber()).isEqualTo(source.getPhoneNumber());
    }

    @AutoParameterizedTest
    void set_correctly_configures_constructor_property_mapping_with_function_that_returns_null(
        UserView source
    ) {
        Mapper mapper = new Mapper(config -> config
            .addMapping(UserView.class, User.class, mapping -> mapping
                .set("passwordHash", x -> null)));

        User actual = mapper.map(source, UserView.class, User.class);

        assertThat(actual.getPasswordHash()).isNull();
    }

    @AutoParameterizedTest
    void set_does_not_allow_duplicate_destination_property_name(
        String sourcePropertyName,
        int destinationPropertyValue
    ) {
        assertThatThrownBy(() ->
            new Mapper(config -> config
                .addMapping(Order.class, OrderView.class, mapping -> mapping
                    .set("numberOfItems", Order::getQuantity)
                    .set("numberOfItems", x -> destinationPropertyValue))));
    }

    @Test
    void set_has_guard_against_destination_property_name() {
        assertThatThrownBy(() ->
            new Mapper(config -> config
                .addMapping(Order.class, OrderView.class, mapping -> mapping
                    .set(null, x -> null))));
    }

    @Test
    void set_has_guard_against_calculator() {
        assertThatThrownBy(() ->
            new Mapper(config -> config
                .addMapping(Order.class, OrderView.class, mapping -> mapping
                    .set("numberOfItems", null))));
    }

    @AutoParameterizedTest
    void addTransform_overrides_existing_transform(String source) {
        Mapper sut = new Mapper(config -> config
            .addTransform(String.class, String.class, x -> x + "1")
            .addTransform(String.class, String.class, x -> x + "2"));

        String actual = sut.map(source, String.class, String.class);

        assertThat(actual).isEqualTo(source + "2");
    }

    @AutoParameterizedTest
    @Repeat(10)
    void addMapping_overrides_existing_mapping(Order source, int quantity) {
        Mapper sut = new Mapper(config -> config
            .addMapping(Order.class, OrderView.class, mapping -> mapping
                .set("numberOfItems", x -> quantity))
            .addMapping(Order.class, OrderView.class, mapping -> mapping
                .set("numberOfItems", Order::getQuantity)));

        OrderView actual = sut.map(source, Order.class, OrderView.class);

        assertThat(actual.getNumberOfItems()).isEqualTo(source.getQuantity());
    }

    @AutoParameterizedTest
    void setParameterNameResolver_is_fluent(String name) {
        new Mapper(c -> assertThat(
            c.setParameterNameResolver(p -> Optional.of(name))).isSameAs(c));
    }

    @Test
    void setParameterNameResolver_has_guard_against_null_value() {
        assertThatThrownBy(() -> new Mapper(c -> c.setParameterNameResolver(null)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void setParameterNameResolver_correctly_configures_resolver(ItemView source) {
        // Arrange
        ParameterNameResolver resolver = p -> {
            if (p.getType().equals(long.class)) {
                return Optional.of("id");
            } else if (p.getType().equals(String.class)) {
                return Optional.of("name");
            } else if (p.getType().equals(Price.class)) {
                return Optional.of("listPrice");
            } else {
                return Optional.empty();
            }
        };

        Mapper sut = new Mapper(c -> c.setParameterNameResolver(resolver));

        // Act
        ItemView actual = sut.map(source, ItemView.class, ItemView.class);

        // Assert
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }
}
