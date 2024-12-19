package easymapper;

final class Projectors extends TypeMapper<Projector<Object, Object>> {

    @SuppressWarnings("unchecked")
    public <S, T> void add(
        Class<S> sourceType,
        Class<T> targetType,
        Projector<S, T> projector
    ) {
        super.add(
            sourceType,
            targetType,
            (context, source, target) -> projector.project(
                context,
                (S) source,
                (T) target
            )
        );
    }

    @SuppressWarnings("unchecked")
    public <S, T> void add(
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
