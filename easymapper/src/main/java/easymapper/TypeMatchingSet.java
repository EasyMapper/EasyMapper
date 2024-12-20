package easymapper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.unmodifiableList;

class TypeMatchingSet<V> {

    private final List<TypeMatchingEntry<V>> entries;

    public TypeMatchingSet(List<TypeMatchingEntry<V>> entries) {
        this.entries = unmodifiableList(new ArrayList<>(entries));
    }

    public Optional<V> find(Type sourceType, Type targetType) {
        for (int i = entries.size() - 1; i >= 0; i--) {
            TypeMatchingEntry<V> entry = entries.get(i);
            if (entry.match(sourceType, targetType)) {
                return Optional.of(entry.value());
            }
        }

        return Optional.empty();
    }
}
