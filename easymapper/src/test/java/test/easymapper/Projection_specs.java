package test.easymapper;

import easymapper.Mapper;
import easymapper.TypeReference;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class Projection_specs {

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
    void map_has_null_guard_for_source(
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
    void map_has_null_guard_for_destination(
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
    void map_has_null_guard_for_source_type(
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
    void map_has_null_guard_for_destination_type(
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

    @Getter
    @Setter
    public static class MutableBag<T> {
        private T value;
    }

    @AutoParameterizedTest
    void map_with_type_reference_has_null_guard_for_source(
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
    void map_with_type_reference_has_null_guard_for_destination(
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
    void map_with_type_reference_has_null_guard_for_source_type_reference(
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
    void map_with_type_reference_has_null_guard_for_destination_type_reference(
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
    void map_with_type_reference_correctly_converts_value_of_type_argument(
        Mapper sut,
        MutableBag<UUID> source,
        MutableBag<String> destination
    ) {
        sut.map(
            source,
            destination,
            new TypeReference<MutableBag<UUID>>() {},
            new TypeReference<MutableBag<String>>() {});

        assertThat(destination.getValue()).isEqualTo(source.getValue().toString());
    }

    @AutoParameterizedTest
    void map_with_type_reference_correctly_maps_value_of_deep_type_argument(
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
}
