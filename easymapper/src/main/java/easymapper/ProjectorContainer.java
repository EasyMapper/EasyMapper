package easymapper;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import static java.util.Collections.unmodifiableList;

class ProjectorContainer {

    @AllArgsConstructor
    @Getter
    @Accessors(fluent = true)
    public static class Entry {

        private final MappingTypePredicate predicate;
        private final ObjectProjector projector;
    }

    private final List<Entry> entries;

    public ProjectorContainer(List<Entry> entries) {
        this.entries = unmodifiableList(entries);
    }

    public Optional<ObjectProjector> find(Type sourceType, Type targetType) {
        for (int i = entries.size() - 1; i >= 0; i--) {
            Entry entry = entries.get(i);
            if (entry.predicate().test(sourceType, targetType)) {
                return Optional.of(entry.projector());
            }
        }

        return Optional.empty();
    }
}
