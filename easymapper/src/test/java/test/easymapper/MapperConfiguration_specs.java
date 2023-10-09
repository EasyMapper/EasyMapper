package test.easymapper;

import autoparams.Repeat;
import easymapper.ConstructorExtractor;
import easymapper.Mapper;
import easymapper.MapperConfiguration;
import easymapper.ParameterNameResolver;
import easymapper.TypeReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import test.easymapper.fixture.HasBrokenConstructor;
import test.easymapper.fixture.ItemView;
import test.easymapper.fixture.Order;
import test.easymapper.fixture.OrderView;
import test.easymapper.fixture.Price;
import test.easymapper.fixture.Pricing;
import test.easymapper.fixture.PricingView;
import test.easymapper.fixture.Recipient;
import test.easymapper.fixture.RecipientView;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MapperConfiguration_specs {

    private static final Function<Type, Boolean> accept = type -> true;

    @AllArgsConstructor
    @Getter
    public static class User {
        private final int id;
        private final String username;
        private final String passwordHash;
    }

    @AllArgsConstructor
    @Getter
    public static class UserView {
        private final int id;
        private final String name;

        public static UserView from(User user) {
            return new UserView(user.getId(), user.getUsername());
        }
    }

    @Test
    void map_with_predicates_has_null_guard_for_source_type_predicate() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.map(null, accept, mapping -> {}));

        assertThatThrownBy(action)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("sourceTypePredicate");
    }

    @Test
    void map_with_predicates_has_null_guard_for_destination_type_predicate() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.map(accept, null, mapping -> {}));

        assertThatThrownBy(action)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("destinationTypePredicate");
    }

    @Test
    void map_with_predicates_has_null_guard_for_mapping_configurer() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.map(accept, accept, null));

        assertThatThrownBy(action)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("configurer");
    }

    @Test
    void map_with_predicates_is_fluent() {
        new Mapper(config ->
            assertThat(config.map(accept, accept, mapping -> {}))
                .isSameAs(config));
    }

    @AutoParameterizedTest
    void map_with_predicates_correctly_works(User user) {
        Mapper mapper = new Mapper(config -> config.map(
            type -> type.equals(User.class),
            type -> type.equals(UserView.class),
            mapping -> mapping.convert(
                source -> context -> UserView.from((User) source))));

        UserView actual = mapper.map(user, User.class, UserView.class);

        assertThat(actual.getId()).isEqualTo(user.getId());
        assertThat(actual.getName()).isEqualTo(user.getUsername());
    }

    @Test
    void map_with_classes_has_null_guard_for_source_type() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.map(null, int.class, mapping -> {}));

        assertThatThrownBy(action)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("sourceType");
    }

    @Test
    void map_with_classes_has_null_guard_for_destination_type() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.map(int.class, null, mapping -> {}));

        assertThatThrownBy(action)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("destinationType");
    }

    @Test
    void map_with_classes_has_null_guard_for_mapping_configurer() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.map(int.class, int.class, null));

        assertThatThrownBy(action)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("configurer");
    }

    @Test
    void map_with_classes_is_fluent() {
        new Mapper(config ->
            assertThat(config.map(int.class, int.class, mapping -> {}))
                .isSameAs(config));
    }

    @Test
    void map_with_type_references_has_null_guard_for_source_type() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.map(
                null,
                new TypeReference<Integer>() {},
                mapping -> {}));

        assertThatThrownBy(action)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("sourceTypeReference");
    }

    @Test
    void map_with_type_references_has_null_guard_for_destination_type() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.map(
                new TypeReference<Integer>() {},
                null,
                mapping -> {}));

        assertThatThrownBy(action)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("destinationTypeReference");
    }

    @Test
    void map_with_type_references_has_null_guard_for_mapping_configurer() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.map(
                new TypeReference<Integer>() {},
                new TypeReference<Integer>() {},
                null));

        assertThatThrownBy(action)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("configurer");
    }

    @Test
    void map_with_type_references_is_fluent() {
        TypeReference<Integer> type = new TypeReference<Integer>() {};
        new Mapper(config ->
            assertThat(config.map(type, type, mapping -> {}))
                .isSameAs(config));
    }

    @AutoParameterizedTest
    void map_with_type_references_correctly_works(User user) {
        Mapper mapper = new Mapper(config -> config.map(
            new TypeReference<User>() {},
            new TypeReference<UserView>() {},
            mapping -> mapping.convert(
                source -> context -> UserView.from(source))));

        UserView actual = mapper.map(user, User.class, UserView.class);

        assertThat(actual.getId()).isEqualTo(user.getId());
        assertThat(actual.getName()).isEqualTo(user.getUsername());
    }

    @Test
    void sut_has_guard_against_null_source_type() {
        assertThatThrownBy(() ->
            new Mapper(config -> config
                .addPropertyMapping(null, OrderView.class, mapping -> {})));
    }

    @Test
    void sut_has_guard_against_null_destination_type() {
        assertThatThrownBy(() ->
            new Mapper(config -> config
                .addPropertyMapping(Order.class, null, mapping -> {})));
    }

    @Test
    void sut_has_guard_against_null_mapping_configurer() {
        assertThatThrownBy(() ->
            new Mapper(config -> config
                .addPropertyMapping(Order.class, OrderView.class, null)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addMapping_sets_source_type() {
        new Mapper(config -> config
            .addPropertyMapping(
                Order.class,
                OrderView.class,
                mapping -> assertThat(mapping.getSourceType()).isEqualTo(Order.class)));
    }

    @Test
    void addMapping_sets_destination_type() {
        new Mapper(config -> config
            .addPropertyMapping(
                Order.class,
                OrderView.class,
                mapping -> assertThat(mapping.getDestinationType()).isEqualTo(OrderView.class)));
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

    @Test
    void addMapping_is_fluent() {
        new Mapper(c -> assertThat(
            c.addPropertyMapping(Order.class, OrderView.class, m -> {})).isSameAs(c));
    }

    @AutoParameterizedTest
    void set_is_fluent(User source) {
        new Mapper(config -> config.addPropertyMapping(
            User.class,
            UserView.class,
            mapping -> assertThat(mapping.set("id", User::getId)).isSameAs(mapping)));
    }

    @AutoParameterizedTest
    void set_correctly_configures_constructor_property_mapping(Pricing source) {
        // Arrange
        Mapper mapper = new Mapper(config -> config
            .addPropertyMapping(Pricing.class, PricingView.class, mapping -> mapping
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
            .addPropertyMapping(Recipient.class, RecipientView.class, mapping -> mapping
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
            .addPropertyMapping(UserView.class, User.class, mapping -> mapping
                .set("username", UserView::getName)
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
                .addPropertyMapping(Order.class, OrderView.class, mapping -> mapping
                    .set("numberOfItems", Order::getQuantity)
                    .set("numberOfItems", x -> destinationPropertyValue))));
    }

    @Test
    void set_has_guard_against_destination_property_name() {
        assertThatThrownBy(() ->
            new Mapper(config -> config
                .addPropertyMapping(Order.class, OrderView.class, mapping -> mapping
                    .set(null, x -> null))));
    }

    @Test
    void set_has_guard_against_calculator() {
        assertThatThrownBy(() ->
            new Mapper(config -> config
                .addPropertyMapping(Order.class, OrderView.class, mapping -> mapping
                    .set("numberOfItems", null))));
    }

    @AutoParameterizedTest
    @Repeat(10)
    void addMapping_overrides_existing_mapping(Order source, int quantity) {
        Mapper sut = new Mapper(config -> config
            .addPropertyMapping(Order.class, OrderView.class, mapping -> mapping
                .set("numberOfItems", x -> quantity))
            .addPropertyMapping(Order.class, OrderView.class, mapping -> mapping
                .set("numberOfItems", Order::getQuantity)));

        OrderView actual = sut.map(source, Order.class, OrderView.class);

        assertThat(actual.getNumberOfItems()).isEqualTo(source.getQuantity());
    }

    @Test
    void apply_has_null_guard_for_configurer() {
        assertThatThrownBy(() -> new Mapper(c -> c.apply(null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("configurer");
    }

    @Test
    void apply_is_fluent() {
        new Mapper(c -> assertThat(c.apply(m -> {})).isSameAs(c));
    }

    @AutoParameterizedTest
    void apply_correctly_configures_mapper(Pricing source) {
        // Arrange
        Consumer<MapperConfiguration> configurer = config -> config
            .addPropertyMapping(Pricing.class, PricingView.class, mapping -> mapping
                .set("salePrice", x -> x.getListPrice() - x.getDiscount()));

        Mapper mapper = new Mapper(config -> config.apply(configurer));

        // Act
        PricingView actual = mapper.map(
            source,
            Pricing.class,
            PricingView.class);

        // Assert
        assertThat(actual.getSalePrice())
            .isEqualTo(source.getListPrice() - source.getDiscount());
    }
}
