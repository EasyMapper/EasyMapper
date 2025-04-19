package test.easymapper;

import easymapper.Mapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

public class SpecsForCollectionMapping {

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

    @Test
    @AutoDomainParams
    void convert_maps_null_iterable_to_null(Mapper sut) {
        val source = new UUIDIterableBag(null);

        StringIterableBag target = sut.convert(
            source,
            StringIterableBag.class
        );

        assertThat(target.getValue()).isNull();
    }

    @Test
    @AutoDomainParams
    void convert_correctly_maps_iterable_constructor_properties(
        Mapper sut,
        UUIDIterableBag source
    ) {
        StringIterableBag target = sut.convert(
            source,
            StringIterableBag.class
        );

        Iterable<String> actual = target.getValue();
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

    @Test
    @AutoDomainParams
    void sut_correctly_maps_collection_constructor_properties(
        Mapper sut,
        UUIDIterableBag source
    ) {
        StringCollectionBag target = sut.convert(
            source,
            StringCollectionBag.class
        );

        Iterable<String> actual = target.getValue();
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

    @Test
    @AutoDomainParams
    void convert_correctly_maps_list_constructor_properties(
        Mapper sut,
        UUIDIterableBag source
    ) {
        StringListBag target = sut.convert(source, StringListBag.class);

        Iterable<String> actual = target.getValue();
        assertThat(actual).isInstanceOf(ArrayList.class);
        assertThat(actual).isEqualTo(StreamSupport
            .stream(source.getValue().spliterator(), false)
            .map(UUID::toString)
            .collect(Collectors.toList()));
    }
}
