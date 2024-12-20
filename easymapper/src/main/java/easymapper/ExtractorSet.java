package easymapper;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toMap;

class ExtractorSet {

    private static class Extractors extends TypeMatchingSet<ObjectExtractor> {
        public Extractors(List<TypeMatchingEntry<ObjectExtractor>> entries) {
            super(entries);
        }
    }

    private final Map<String, Extractors> entries;

    public ExtractorSet(
        Map<String, List<TypeMatchingEntry<ObjectExtractor>>> entries
    ) {
        this.entries = unmodifiableMap(
            entries.entrySet().stream().collect(
                toMap(
                    Entry::getKey,
                    entry -> new Extractors(entry.getValue())
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
            .flatMap(extractors -> extractors.find(sourceType, targetType));
    }
}
