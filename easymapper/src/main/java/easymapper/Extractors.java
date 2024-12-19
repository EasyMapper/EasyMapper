package easymapper;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

final class Extractors {

    private static class Container extends TypeMapper<Extractor<Object>> { }

    private final Map<String, Container> store = new HashMap<>();

    public Optional<Extractor<Object>> find(
        Type sourceType,
        Type targetType,
        String targetPropertyName
    ) {
        return Optional
            .ofNullable(store.get(targetPropertyName))
            .flatMap(container -> container.find(sourceType, targetType));
    }

    @SuppressWarnings("unchecked")
    public <S, T> void add(
        Class<S> sourceType,
        Class<T> targetType,
        String targetPropertyName,
        Extractor<S> extractor
    ) {
        Container container = store.computeIfAbsent(
            targetPropertyName,
            key -> new Container()
        );

        container.add(
            sourceType,
            targetType,
            (context, source) -> extractor.extract(context, (S) source)
        );
    }

    public void addRange(Extractors extractors) {
        for (Entry<String, Container> entry : extractors.store.entrySet()) {
            this.store
                .computeIfAbsent(entry.getKey(), key -> new Container())
                .addRange(entry.getValue());
        }
    }
}
