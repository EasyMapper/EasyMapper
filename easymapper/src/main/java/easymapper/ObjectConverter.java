package easymapper;

import java.util.function.Function;

@FunctionalInterface
interface ObjectConverter extends Converter<Object, Object> {

    @SuppressWarnings("unchecked")
    static <S, T> ObjectConverter from(Converter<S, T> converter) {
        return (source, context) -> converter.convert((S) source, context);
    }

    default Function<Object, Object> bindContext(MappingContext context) {
        return source -> convert(source, context);
    }
}
