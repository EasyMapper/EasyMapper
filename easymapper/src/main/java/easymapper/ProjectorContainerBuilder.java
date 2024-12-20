package easymapper;

import java.util.ArrayList;
import java.util.List;

import easymapper.ProjectorContainer.Entry;

class ProjectorContainerBuilder {

    private final List<Entry> entries = new ArrayList<>();

    public <S, T> void add(
        Class<S> sourceType,
        Class<T> targetType,
        Projector<S, T> projector
    ) {
        entries.add(
            new Entry(
                new MappingTypePredicate(
                    TypePredicate.from(sourceType),
                    TypePredicate.from(targetType)
                ),
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
            new Entry(
                new MappingTypePredicate(
                    sourceTypePredicate,
                    targetTypePredicate
                ),
                ObjectProjector.from(projector)
            )
        );
    }

    public ProjectorContainer build() {
        return new ProjectorContainer(entries);
    }
}
