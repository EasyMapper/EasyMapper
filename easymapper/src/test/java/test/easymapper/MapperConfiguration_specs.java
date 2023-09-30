package test.easymapper;

import autoparams.Repeat;
import easymapper.ConstructorExtractor;
import easymapper.Mapper;
import easymapper.MapperConfiguration;
import easymapper.ParameterNameResolver;
import java.lang.reflect.Constructor;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static easymapper.MapperConfiguration.configureMapper;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparingInt;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MapperConfiguration_specs {

    @Test
    void configureMapper_returns_mapper_configuration() {
        MapperConfiguration actual = configureMapper(builder -> { });
        assertThat(actual).isNotNull();
    }

    @Test
    void sut_has_guard_against_null_mapper_configurer() {
        assertThatThrownBy(() -> configureMapper(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void sut_has_guard_against_null_source_type() {
        assertThatThrownBy(() ->
            configureMapper(config -> config
                .addMapping(null, OrderView.class, mapping -> { })));
    }

    @Test
    void sut_has_guard_against_null_destination_type() {
        assertThatThrownBy(() ->
            configureMapper(config -> config
                .addMapping(Order.class, null, mapping -> { })));
    }

    @Test
    void sut_has_guard_against_null_mapping_configurer() {
        assertThatThrownBy(() ->
            configureMapper(config -> config
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
        Mapper mapper = new Mapper(configureMapper(c -> c.setConstructorExtractor(extractor)));

        // Act
        HasBrokenConstructor actual = mapper.map(source, HasBrokenConstructor.class);

        // Assert
        assertThat(actual.getId()).isEqualTo(source.getId());
        assertThat(actual.getUsername()).isEqualTo(HasBrokenConstructor.DEFAULT_USERNAME);
    }

    @AutoParameterizedTest
    void setConstructorExtractor_is_fluent() {
        new Mapper(configureMapper(c -> assertThat(
            c.setConstructorExtractor(c.getConstructorExtractor())).isSameAs(c)));
    }

    @AutoParameterizedTest
    void setConstructorExtractor_has_guard_against_null_value() {
        assertThatThrownBy(() -> configureMapper(c -> c.setConstructorExtractor(null)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addMapping_is_fluent() {
        new Mapper(configureMapper(c -> assertThat(
            c.addMapping(Order.class, OrderView.class, m -> { })).isSameAs(c)));
    }

    @Test
    void addTransform_is_fluent() {
        new Mapper(configureMapper(c -> assertThat(
            c.addTransform(int.class, int.class, identity())).isSameAs(c)));
    }

    @Test
    void addTransform_has_guard_against_null_source_type() {
        assertThatThrownBy(() ->
            configureMapper(c -> c.addTransform(null, int.class, identity())))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addTransform_has_guard_against_null_destination_type() {
        assertThatThrownBy(() ->
            configureMapper(c -> c.addTransform(int.class, null, identity())))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addTransform_has_guard_against_null_function() {
        assertThatThrownBy(() ->
            configureMapper(c -> c.addTransform(int.class, int.class, null)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void addTransform_correctly_adds_transform(Pricing source) {
        // Arrange
        Mapper sut = new Mapper(configureMapper(config -> config
            .addTransform(
                Pricing.class,
                PricingView.class,
                pricing -> new PricingView(
                    pricing.getListPrice(),
                    pricing.getDiscount(),
                    pricing.getListPrice() - pricing.getDiscount()))));

        // Act
        PricingView actual = sut.map(source, PricingView.class);

        // Assert
        assertThat(actual.getSalePrice())
            .isEqualTo(source.getListPrice() - source.getDiscount());
    }

    @AutoParameterizedTest
    void set_is_fluent(User source) {
        configureMapper(config -> config.addMapping(
            User.class,
            UserView.class,
            mapping -> assertThat(mapping.set("id", User::getId)).isSameAs(mapping)));
    }

    @AutoParameterizedTest
    void set_correctly_configures_constructor_property_mapping(Pricing source) {
        // Arrange
        Mapper mapper = new Mapper(configureMapper(config -> config
            .addMapping(Pricing.class, PricingView.class, mapping -> mapping
                .set("salePrice", x -> x.getListPrice() - x.getDiscount()))));

        // Act
        PricingView actual = mapper.map(source, PricingView.class);

        // Assert
        assertThat(actual.getSalePrice())
            .isEqualTo(source.getListPrice() - source.getDiscount());
    }

    @AutoParameterizedTest
    void set_correctly_configures_settable_property_mapping(Recipient source) {
        // Arrange
        Mapper mapper = new Mapper(configureMapper(config -> config
            .addMapping(Recipient.class, RecipientView.class, mapping -> mapping
                .set("recipientName", Recipient::getName)
                .set("recipientPhoneNumber", Recipient::getPhoneNumber))));

        // Act
        RecipientView actual = mapper.map(source, RecipientView.class);

        // Assert
        assertThat(actual.getRecipientName()).isEqualTo(source.getName());
        assertThat(actual.getRecipientPhoneNumber()).isEqualTo(source.getPhoneNumber());
    }

    @AutoParameterizedTest
    void set_does_not_allow_duplicate_destination_property_name(
        String sourcePropertyName,
        int destinationPropertyValue
    ) {
        assertThatThrownBy(() ->
            configureMapper(config -> config
                .addMapping(Order.class, OrderView.class, mapping -> mapping
                    .set("numberOfItems", Order::getQuantity)
                    .set("numberOfItems", x -> destinationPropertyValue))));
    }

    @Test
    void set_has_guard_against_destination_property_name() {
        assertThatThrownBy(() ->
            configureMapper(config -> config
                .addMapping(Order.class, OrderView.class, mapping -> mapping
                    .set(null, x -> null))));
    }

    @Test
    void set_has_guard_against_calculator() {
        assertThatThrownBy(() ->
            configureMapper(config -> config
                .addMapping(Order.class, OrderView.class, mapping -> mapping
                    .set("numberOfItems", null))));
    }

    @AutoParameterizedTest
    void addTransform_overrides_existing_transform(String source) {
        Mapper sut = new Mapper(configureMapper(config -> config
            .addTransform(String.class, String.class, x -> x + "1")
            .addTransform(String.class, String.class, x -> x + "2")));

        String actual = sut.map(source, String.class);

        assertThat(actual).isEqualTo(source + "2");
    }

    @AutoParameterizedTest
    @Repeat(10)
    void addMapping_overrides_existing_mapping(Order source, int quantity) {
        Mapper sut = new Mapper(configureMapper(config -> config
            .addMapping(Order.class, OrderView.class, mapping -> mapping
                .set("numberOfItems", x -> quantity))
            .addMapping(Order.class, OrderView.class, mapping -> mapping
                .set("numberOfItems", Order::getQuantity))));

        OrderView actual = sut.map(source, OrderView.class);

        assertThat(actual.getNumberOfItems()).isEqualTo(source.getQuantity());
    }

    @AutoParameterizedTest
    void setParameterNameResolver_is_fluent(String name) {
        new Mapper(configureMapper(c -> assertThat(
            c.setParameterNameResolver(p -> Optional.of(name))).isSameAs(c)));
    }

    @Test
    void setParameterNameResolver_has_guard_against_null_value() {
        assertThatThrownBy(() -> configureMapper(c -> c.setParameterNameResolver(null)))
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

        Mapper sut = new Mapper(configureMapper(c -> c
            .setParameterNameResolver(resolver)));

        // Act
        ItemView actual = sut.map(source, ItemView.class);

        // Assert
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }
}
