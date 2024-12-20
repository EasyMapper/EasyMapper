package easymapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import easymapper.ExtractorContainer.Entry;

class ExtractorContainerBuilder {

    private final Map<String, List<Entry>> entries = new HashMap<>();

    public <S, P> void add(
        TypePredicate sourceTypePredicate,
        TypePredicate propertyTypePredicate,
        String targetPropertyName,
        Extractor<S, P> extractor
    ) {
        List<Entry> list = entries.computeIfAbsent(
            targetPropertyName,
            key -> new ArrayList<>()
        );

        list.add(
            new Entry(
                new MappingTypePredicate(
                    sourceTypePredicate,
                    propertyTypePredicate
                ),
                ObjectExtractor.from(extractor)
            )
        );
    }

    public ExtractorContainer build() {
        return new ExtractorContainer(entries);
    }
}
