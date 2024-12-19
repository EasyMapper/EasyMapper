package easymapper;

final class Projectors extends TypeMapper<Projector<Object, Object>> {

    public <S, T> void add(
        Class<S> sourceType,
        Class<T> targetType,
        Projector<S, T> projector
    ) {
        addProjector(
            TypePredicate.from(sourceType),
            TypePredicate.from(targetType),
            projector
        );
    }

    public <S, T> void add(
        TypePredicate sourceTypePredicate,
        TypePredicate targetTypePredicate,
        Projector<S, T> projector
    ) {
        addProjector(sourceTypePredicate, targetTypePredicate, projector);
    }

    @SuppressWarnings("unchecked")
    private <S, T> void addProjector(
        TypePredicate sourceTypePredicate,
        TypePredicate targetTypePredicate,
        Projector<S, T> projector
    ) {
        super.add(
            sourceTypePredicate,
            targetTypePredicate,
            (context, source, target) -> projector.project(
                context,
                (S) source,
                (T) target
            )
        );
    }
}
