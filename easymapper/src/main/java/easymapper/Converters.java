package easymapper;

final class Converters extends TypeMapper<Converter<Object, Object>> {

    public <S, T> void add(
        Class<S> sourceType,
        Class<T> targetType,
        Converter<S, T> converter
    ) {
        addConverter(
            TypePredicate.from(sourceType),
            TypePredicate.from(targetType),
            converter
        );
    }

    public <S, T> void add(
        TypePredicate sourceTypePredicate,
        TypePredicate targetTypePredicate,
        Converter<S, T> converter
    ) {
        addConverter(sourceTypePredicate, targetTypePredicate, converter);
    }

    @SuppressWarnings("unchecked")
    private <S, T> void addConverter(
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
