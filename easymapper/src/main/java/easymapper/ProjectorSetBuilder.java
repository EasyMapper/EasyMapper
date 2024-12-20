package easymapper;

import java.util.ArrayList;
import java.util.List;

class ProjectorSetBuilder {

    private final List<TypeMatchingEntry<ObjectProjector>> entries;

    public ProjectorSetBuilder() {
        entries = new ArrayList<>();
    }

    public <S, T> void add(
        Class<S> sourceType,
        Class<T> targetType,
        Projector<S, T> projector
    ) {
        entries.add(
            new TypeMatchingEntry<>(
                TypePredicate.from(sourceType),
                TypePredicate.from(targetType),
                ObjectProjector.from(projector)
            )
        );
    }

    public <S, T> void add(
        TypePredicate sourceTypePredicate,
        TypePredicate targetTypePredicate,
        Projector<S, T> projector
    ) {
        entries.add(
            new TypeMatchingEntry<>(
                sourceTypePredicate,
                targetTypePredicate,
                ObjectProjector.from(projector)
            )
        );
    }

    public ProjectorSet build() {
        return new ProjectorSet(entries);
    }
}
