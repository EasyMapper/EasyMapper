package easymapper;

final class Converters extends TypeMapper<Converter<Object, Object>> {

    @SuppressWarnings("unchecked")
    public <S, T> void add(
        Class<S> sourceType,
        Class<T> targetType,
        Converter<S, T> converter
    ) {
        super.add(
            sourceType,
            targetType,
            (context, source) -> converter.convert(context, (S) source)
        );
    }

    @SuppressWarnings("unchecked")
    public <S, T> void add(
        TypePredicate sourceTypePredicate,
        TypePredicate targetTypePredicate,
        Converter<S, T> converter
    ) {
        super.add(
            sourceTypePredicate,
            targetTypePredicate,
            (context, source) -> converter.convert(context, (S) source)
        );
    }
}
