package easymapper;

@FunctionalInterface
public interface Converter<S, T> {

    T convert(S source, MappingContext context);

    @SuppressWarnings("unchecked")
    static <S, T> Converter<S, T> identity() {
        return (source, context) -> (T) source;
    }
}
