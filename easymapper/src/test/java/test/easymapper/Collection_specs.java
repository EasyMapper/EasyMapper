package test.easymapper;

import easymapper.Mapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.assertj.core.api.Assertions.assertThat;

public class Collection_specs {

    @AllArgsConstructor
    @Getter
    public static class UUIDIterableBag {
        private final Iterable<UUID> value;
    }

    @AllArgsConstructor
    @Getter
    public static class StringIterableBag {
        private final Iterable<String> value;
    }

    @AutoParameterizedTest
    void sut_maps_null_iterable_to_null(Mapper sut) {
        UUIDIterableBag source = new UUIDIterableBag(null);

        StringIterableBag destination = sut.map(
            source,
            UUIDIterableBag.class,
            StringIterableBag.class);

        assertThat(destination.getValue()).isNull();
    }

    @AutoParameterizedTest
    void sut_correctly_maps_iterable_constructor_properties(
        Mapper sut,
        UUIDIterableBag source
    ) {
        StringIterableBag destination = sut.map(
            source,
            UUIDIterableBag.class,
            StringIterableBag.class);

        Iterable<String> actual = destination.getValue();
        assertThat(actual).isInstanceOf(ArrayList.class);
        assertThat(actual).isEqualTo(StreamSupport
            .stream(source.getValue().spliterator(), false)
            .map(UUID::toString)
            .collect(Collectors.toList()));
    }

    @AllArgsConstructor
    @Getter
    public static class StringCollectionBag {
        private final Collection<String> value;
    }

    @AutoParameterizedTest
    void sut_correctly_maps_collection_constructor_properties(
        Mapper sut,
        UUIDIterableBag source
    ) {
        StringCollectionBag destination = sut.map(
            source,
            UUIDIterableBag.class,
            StringCollectionBag.class);

        Iterable<String> actual = destination.getValue();
        assertThat(actual).isInstanceOf(ArrayList.class);
        assertThat(actual).isEqualTo(StreamSupport
            .stream(source.getValue().spliterator(), false)
            .map(UUID::toString)
            .collect(Collectors.toList()));
    }

    @AllArgsConstructor
    @Getter
    public static class StringListBag {
        private final List<String> value;
    }

    @AutoParameterizedTest
    void sut_correctly_maps_list_constructor_properties(
        Mapper sut,
        UUIDIterableBag source
    ) {
        StringListBag destination = sut.map(
            source,
            UUIDIterableBag.class,
            StringListBag.class);

        Iterable<String> actual = destination.getValue();
        assertThat(actual).isInstanceOf(ArrayList.class);
        assertThat(actual).isEqualTo(StreamSupport
            .stream(source.getValue().spliterator(), false)
            .map(UUID::toString)
            .collect(Collectors.toList()));
    }
}
