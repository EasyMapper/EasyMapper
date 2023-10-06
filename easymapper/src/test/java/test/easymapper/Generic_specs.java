package test.easymapper;

import easymapper.Mapper;
import easymapper.TypeReference;
import test.easymapper.fixture.ImmutableBag;
import test.easymapper.fixture.MutableBag;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class Generic_specs {

    @AutoParameterizedTest
    void mapper_correctly_maps_value_of_type_argument_for_constructors(
        Mapper sut,
        ImmutableBag<UUID> source
    ) {
        ImmutableBag<String> actual = sut.map(
            source,
            new TypeReference<ImmutableBag<UUID>>() { },
            new TypeReference<ImmutableBag<String>>() { });

        assertThat(actual).isNotNull();
        assertThat(actual.getValue()).isEqualTo(source.getValue().toString());
    }

    @AutoParameterizedTest
    void mapper_correctly_maps_value_of_type_argument_for_setters(
        Mapper sut,
        MutableBag<UUID> source
    ) {
        MutableBag<String> actual = sut.map(
            source,
            new TypeReference<MutableBag<UUID>>() { },
            new TypeReference<MutableBag<String>>() { });

        assertThat(actual).isNotNull();
        assertThat(actual.getValue()).isEqualTo(source.getValue().toString());
    }

    @AutoParameterizedTest
    void mapper_correctly_maps_value_of_type_argument_for_setters_of_existing_instance(
        Mapper sut,
        MutableBag<UUID> source,
        MutableBag<String> destination
    ) {
        sut.map(
            source,
            destination,
            new TypeReference<MutableBag<UUID>>() { },
            new TypeReference<MutableBag<String>>() { });

        assertThat(destination.getValue()).isEqualTo(source.getValue().toString());
    }

    @AutoParameterizedTest
    void mapper_correctly_maps_value_of_deep_type_argument_for_constructors(
        Mapper sut,
        ImmutableBag<ImmutableBag<UUID>> source
    ) {
        ImmutableBag<ImmutableBag<String>> actual = sut.map(
            source,
            new TypeReference<ImmutableBag<ImmutableBag<UUID>>>() { },
            new TypeReference<ImmutableBag<ImmutableBag<String>>>() { });

        assertThat(actual).isNotNull();
        assertThat(actual.getValue().getValue())
            .isEqualTo(source.getValue().getValue().toString());
    }

    @AutoParameterizedTest
    void mapper_correctly_maps_value_of_deep_type_argument_for_setters(
        Mapper sut,
        MutableBag<MutableBag<UUID>> source
    ) {
        MutableBag<MutableBag<String>> actual = sut.map(
            source,
            new TypeReference<MutableBag<MutableBag<UUID>>>() { },
            new TypeReference<MutableBag<MutableBag<String>>>() { });

        assertThat(actual).isNotNull();
        assertThat(actual.getValue().getValue())
            .isEqualTo(source.getValue().getValue().toString());
    }

    @AutoParameterizedTest
    void mapper_correctly_maps_value_of_deep_type_argument_for_setters_of_existing_instance(
        Mapper sut,
        MutableBag<MutableBag<UUID>> source,
        MutableBag<MutableBag<String>> destination
    ) {
        sut.map(
            source,
            destination,
            new TypeReference<MutableBag<MutableBag<UUID>>>() { },
            new TypeReference<MutableBag<MutableBag<String>>>() { });

        assertThat(destination.getValue().getValue())
            .isEqualTo(source.getValue().getValue().toString());
    }

    @AutoParameterizedTest
    void map_has_null_guard_for_destination_type_reference(
        Mapper sut,
        ImmutableBag<UUID> source
    ) {
        TypeReference<ImmutableBag<String>> destinationTypeReference = null;
        assertThatThrownBy(() -> sut.map(
            source,
            new TypeReference<ImmutableBag<UUID>>() { },
            destinationTypeReference))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void map_has_null_guard_for_source_type_reference(
        Mapper sut,
        ImmutableBag<UUID> source
    ) {
        TypeReference<ImmutableBag<UUID>> sourceTypeReference = null;
        assertThatThrownBy(() -> sut.map(
            source,
            sourceTypeReference,
            new TypeReference<ImmutableBag<String>>() { }))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void projecting_map_has_null_guard_for_source(
        Mapper sut,
        MutableBag<String> destination
    ) {
        assertThatThrownBy(() -> sut.map(
            null,
            destination,
            new TypeReference<MutableBag<UUID>>() { },
            new TypeReference<MutableBag<String>>() { }))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void projecting_map_has_null_guard_for_destination(
        Mapper sut,
        MutableBag<UUID> source
    ) {
        assertThatThrownBy(() -> sut.map(
            source,
            null,
            new TypeReference<MutableBag<UUID>>() { },
            new TypeReference<MutableBag<String>>() { }))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void projecting_map_has_null_guard_for_source_type_reference(
        Mapper sut,
        MutableBag<UUID> source,
        MutableBag<String> destination
    ) {
        TypeReference<MutableBag<UUID>> sourceTypeReference = null;
        assertThatThrownBy(() -> sut.map(
            source,
            destination,
            sourceTypeReference,
            new TypeReference<MutableBag<String>>() { }))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void projecting_map_has_null_guard_for_destination_type_reference(
        Mapper sut,
        MutableBag<UUID> source,
        MutableBag<String> destination
    ) {
        TypeReference<MutableBag<String>> destinationTypeReference = null;
        assertThatThrownBy(() -> sut.map(
            source,
            destination,
            new TypeReference<MutableBag<UUID>>() { },
            destinationTypeReference))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
