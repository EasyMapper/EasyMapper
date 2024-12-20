package easymapper;

import java.util.ArrayList;
import java.util.List;

import easymapper.ConverterContainer.Entry;

class ConverterContainerBuilder {

    private final List<Entry> entries = new ArrayList<>();

    public <S, T> void add(
        TypePredicate sourceTypePredicate,
        TypePredicate targetTypePredicate,
        Converter<S, T> converter
    ) {
        entries.add(
            new Entry(
                new MappingTypePredicate(
                    sourceTypePredicate,
                    targetTypePredicate
                ),
                ObjectConverter.from(converter)
            )
        );
    }

    public ConverterContainer build() {
        return new ConverterContainer(entries);
    }
}
