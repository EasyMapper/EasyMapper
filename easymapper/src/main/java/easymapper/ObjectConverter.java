package easymapper;

import java.util.function.Function;

@FunctionalInterface
interface ObjectConverter extends Converter<Object, Object> {

    @SuppressWarnings("unchecked")
    static <S, T> ObjectConverter from(Converter<S, T> converter) {
        return (context, source) -> converter.convert(context, (S) source);
    }

    default Function<Object, Object> bindContext(MappingContext context) {
        return source -> convert(context, source);
    }
}
