package test.easymapper;

import easymapper.Mapper;
import easymapper.TypeReference;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Mapper_specs {

    @Test
    void constructor_has_guard_against_null_configurer() {
        assertThatThrownBy(() -> new Mapper(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AllArgsConstructor
    @Getter
    public static class User {
        private final long id;
        private final String username;
        private final String passwordHash;
    }

    @Getter
    @Setter
    public static class UserView {
        private long id;
        private String username;
    }

    @AutoParameterizedTest
    void projecting_map_with_classes_has_null_guard_for_source(
        Mapper sut,
        UserView destination
    ) {
        User source = null;
        assertThatThrownBy(
            () -> sut.map(source, destination, User.class, UserView.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("source");
    }

    @AutoParameterizedTest
    void projecting_map_with_classes_has_null_guard_for_destination(
        Mapper sut,
        User source
    ) {
        UserView destination = null;
        assertThatThrownBy(
            () -> sut.map(source, destination, User.class, UserView.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("destination");
    }

    @AutoParameterizedTest
    void projecting_map_with_classes_has_null_guard_for_source_type(
        Mapper sut,
        User source,
        UserView destination
    ) {
        Class<User> sourceType = null;
        assertThatThrownBy(
            () -> sut.map(source, destination, sourceType, UserView.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("sourceType");
    }

    @AutoParameterizedTest
    void projecting_map_with_classes_has_null_guard_for_destination_type(
        Mapper sut,
        User source,
        UserView destination
    ) {
        Class<UserView> destinationType = null;
        assertThatThrownBy(
            () -> sut.map(source, destination, User.class, destinationType))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("destinationType");
    }

    @AutoParameterizedTest
    void projection_map_with_classes_correctly_maps_properties(
        Mapper sut,
        User source
    ) {
        UserView destination = new UserView();

        sut.map(source, destination, User.class, UserView.class);

        assertThat(destination.getId()).isEqualTo(source.getId());
        assertThat(destination.getUsername()).isEqualTo(source.getUsername());
    }

    @AllArgsConstructor
    @Getter
    public static class Pricing {
        private final double listPrice;
        private final double discount;
    }

    @Getter
    @Setter
    public static class PricingView {
        private double listPrice;
        private double discount;
        private double salePrice;
    }

    @AutoParameterizedTest
    void projecting_map_with_classes_fails_for_missing_property(
        Mapper sut,
        Pricing source,
        PricingView destination
    ) {
        assertThatThrownBy(() -> sut.map(
            source,
            destination,
            Pricing.class,
            PricingView.class))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("salePrice");
    }

    @Getter
    @Setter
    public static class MutableBag<T> {
        private T value;
    }

    @AutoParameterizedTest
    void projecting_map_with_type_reference_has_null_guard_for_source(
        Mapper sut,
        MutableBag<String> destination
    ) {
        assertThatThrownBy(() -> sut.map(
            null,
            destination,
            new TypeReference<MutableBag<UUID>>() {},
            new TypeReference<MutableBag<String>>() {}))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void projecting_map_with_type_reference_has_null_guard_for_destination(
        Mapper sut,
        MutableBag<UUID> source
    ) {
        assertThatThrownBy(() -> sut.map(
            source,
            null,
            new TypeReference<MutableBag<UUID>>() {},
            new TypeReference<MutableBag<String>>() {}))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void projecting_map_with_type_reference_has_null_guard_for_source_type_reference(
        Mapper sut,
        MutableBag<UUID> source,
        MutableBag<String> destination
    ) {
        TypeReference<MutableBag<UUID>> sourceTypeReference = null;
        assertThatThrownBy(() -> sut.map(
            source,
            destination,
            sourceTypeReference,
            new TypeReference<MutableBag<String>>() {}))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void projecting_map_with_type_reference_has_null_guard_for_destination_type_reference(
        Mapper sut,
        MutableBag<UUID> source,
        MutableBag<String> destination
    ) {
        TypeReference<MutableBag<String>> destinationTypeReference = null;
        assertThatThrownBy(() -> sut.map(
            source,
            destination,
            new TypeReference<MutableBag<UUID>>() {},
            destinationTypeReference))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void projecting_map_with_type_reference_correctly_converts_value_of_type_argument(
        Mapper sut,
        MutableBag<UUID> source,
        MutableBag<String> destination
    ) {
        sut.map(
            source,
            destination,
            new TypeReference<MutableBag<UUID>>() {},
            new TypeReference<MutableBag<String>>() {});

        assertThat(destination.getValue())
            .isEqualTo(source.getValue().toString());
    }

    @AutoParameterizedTest
    void projecting_map_with_type_reference_correctly_maps_value_of_deep_type_argument(
        Mapper sut,
        MutableBag<MutableBag<UUID>> source,
        MutableBag<MutableBag<String>> destination
    ) {
        sut.map(
            source,
            destination,
            new TypeReference<MutableBag<MutableBag<UUID>>>() {},
            new TypeReference<MutableBag<MutableBag<String>>>() {});

        assertThat(destination.getValue().getValue())
            .isEqualTo(source.getValue().getValue().toString());
    }

    @AutoParameterizedTest
    void projecting_map_with_no_type_hint_has_null_guard_for_source(
        Mapper sut,
        User destination
    ) {
        assertThatThrownBy(() -> sut.map(null, destination))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("source");
    }

    @AutoParameterizedTest
    void projecting_map_with_no_type_hint_has_null_guard_for_destination(
        Mapper sut,
        User source
    ) {
        assertThatThrownBy(() -> sut.map(source, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("destination");
    }

    @AutoParameterizedTest
    void projecting_map_with_no_type_hint_correctly_maps_properties(
        Mapper sut,
        User source
    ) {
        UserView destination = new UserView();

        sut.map(source, destination);

        assertThat(destination.getId()).isEqualTo(source.getId());
        assertThat(destination.getUsername()).isEqualTo(source.getUsername());
    }
}
