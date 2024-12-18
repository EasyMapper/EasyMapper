package easymapper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

abstract class TypeMapper<V> {

    private final List<Entry<V>> entries = new ArrayList<>();

    @AllArgsConstructor
    @Getter
    @Accessors(fluent = true)
    private static class Entry<V> {

        private final TypePredicate sourceTypePredicate;
        private final TypePredicate targetTypePredicate;
        private final V value;

        public boolean match(Type sourceType, Type targetType) {
            return sourceTypePredicate.test(sourceType)
                && targetTypePredicate.test(targetType);
        }
    }

    public Optional<V> find(Type sourceType, Type targetType) {
        for (int i = entries.size() - 1; i >= 0; i--) {
            Entry<V> entry = entries.get(i);
            if (entry.match(sourceType, targetType)) {
                return Optional.of(entry.value());
            }
        }
        return Optional.empty();
    }

    public <S, T> void add(Class<S> sourceType, Class<T> targetType, V value) {
        val entry = new Entry<V>(
            TypePredicate.from(sourceType),
            TypePredicate.from(targetType),
            value
        );
        entries.add(entry);
    }

    public void addRange(TypeMapper<V> other) {
        entries.addAll(other.entries);
    }
}
