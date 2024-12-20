package easymapper;

import java.util.ArrayList;
import java.util.List;

class ConverterSetBuilder {

    private final List<TypeMatchingEntry<ObjectConverter>> entries;

    public ConverterSetBuilder() {
        entries = new ArrayList<>();
    }

    public <S, T> void add(
        Class<S> sourceType,
        Class<T> targetType,
        Converter<S, T> converter
    ) {
        entries.add(
            new TypeMatchingEntry<>(
                TypePredicate.from(sourceType),
                TypePredicate.from(targetType),
                ObjectConverter.from(converter)
            )
        );
    }

    public <S, T> void add(
        TypePredicate sourceTypePredicate,
        TypePredicate targetTypePredicate,
        Converter<S, T> converter
    ) {
        entries.add(
            new TypeMatchingEntry<>(
                sourceTypePredicate,
                targetTypePredicate,
                ObjectConverter.from(converter)
            )
        );
    }

    public ConverterSet build() {
        return new ConverterSet(entries);
    }
}
