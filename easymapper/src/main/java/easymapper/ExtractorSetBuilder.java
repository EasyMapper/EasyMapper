package easymapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ExtractorSetBuilder {

    private final Map<String, List<TypeMatchingEntry<ObjectExtractor>>> entries;

    public ExtractorSetBuilder() {
        entries = new HashMap<>();
    }

    public <S, P> void add(
        Class<S> sourceType,
        Class<?> targetType,
        String targetPropertyName,
        Extractor<S, P> extractor
    ) {
        addExtractor(
            TypePredicate.from(sourceType),
            TypePredicate.from(targetType),
            targetPropertyName,
            extractor
        );
    }

    public <S, P> void add(
        TypePredicate sourceTypePredicate,
        TypePredicate targetTypePredicate,
        String targetPropertyName,
        Extractor<S, P> extractor
    ) {
        addExtractor(
            sourceTypePredicate,
            targetTypePredicate,
            targetPropertyName,
            extractor
        );
    }

    private <S, P> void addExtractor(
        TypePredicate sourceTypePredicate,
        TypePredicate targetTypePredicate,
        String targetPropertyName,
        Extractor<S, P> extractor
    ) {
        List<TypeMatchingEntry<ObjectExtractor>> list = entries.computeIfAbsent(
            targetPropertyName,
            key -> new ArrayList<>()
        );

        list.add(
            new TypeMatchingEntry<>(
                sourceTypePredicate,
                targetTypePredicate,
                ObjectExtractor.from(extractor)
            )
        );
    }

    public ExtractorSet build() {
        return new ExtractorSet(entries);
    }
}
