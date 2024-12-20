package easymapper;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toMap;

class ExtractorContainer {

    @AllArgsConstructor
    @Getter
    @Accessors(fluent = true)
    public static class Entry {

        private final MappingTypePredicate predicate;
        private final ObjectExtractor extractor;
    }

    private final Map<String, List<Entry>> entries;

    public ExtractorContainer(Map<String, List<Entry>> entries) {
        this.entries = unmodifiableMap(
            entries.keySet().stream().collect(
                toMap(
                    key -> key,
                    key -> unmodifiableList(entries.get(key))
                )
            )
        );
    }

    public Optional<ObjectExtractor> find(
        Type sourceType,
        Type targetType,
        String targetPropertyName
    ) {
        return Optional
            .ofNullable(entries.get(targetPropertyName))
            .flatMap(list -> find(list, sourceType, targetType));
    }

    private static Optional<ObjectExtractor> find(
        List<Entry> entries,
        Type sourceType,
        Type targetType
    ) {
        for (int i = entries.size() - 1; i >= 0; i--) {
            Entry entry = entries.get(i);
            if (entry.predicate().test(sourceType, targetType)) {
                return Optional.of(entry.extractor());
            }
        }

        return Optional.empty();
    }
}
