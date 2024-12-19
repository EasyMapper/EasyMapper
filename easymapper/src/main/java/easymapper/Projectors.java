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
}
