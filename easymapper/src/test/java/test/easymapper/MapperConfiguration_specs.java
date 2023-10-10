package test.easymapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import easymapper.ConstructorExtractor;
import easymapper.Mapper;
import easymapper.MapperConfiguration;
import easymapper.ParameterNameResolver;
import easymapper.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import test.easymapper.fixture.HasBrokenConstructor;
import test.easymapper.fixture.ItemView;
import test.easymapper.fixture.Price;
import test.easymapper.fixture.Pricing;
import test.easymapper.fixture.PricingView;

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
    void map_with_classes_overwrites_existing_map() {
        Mapper mapper = new Mapper(config -> config
            .map(String.class, String.class, mapping -> mapping
                .convert(source -> context -> source + "1"))
            .map(String.class, String.class, mapping -> mapping
                .convert(source -> context -> source + "2")));

        String actual = mapper.map("0", String.class, String.class);

        assertThat(actual).isEqualTo("02");
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
    void setConstructorExtractor_correctly_configures_extractor(User source) {
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
    void apply_correctly_configures_mapper(Pricing pricing) {
        // Arrange
        Consumer<MapperConfiguration> configurer = config -> config
            .map(Pricing.class, PricingView.class, mapping -> mapping
                .compute(
                    "salePrice",
                    source -> context -> source.getListPrice() - source.getDiscount()));

        Mapper mapper = new Mapper(config -> config.apply(configurer));

        // Act
        PricingView actual = mapper.map(
            pricing,
            Pricing.class,
            PricingView.class);

        // Assert
        assertThat(actual.getSalePrice())
            .isEqualTo(pricing.getListPrice() - pricing.getDiscount());
    }
}
