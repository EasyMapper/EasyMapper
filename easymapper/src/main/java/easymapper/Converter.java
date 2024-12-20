package easymapper;

@FunctionalInterface
public interface Converter<S, T> {

    T convert(MappingContext context, S source);

    @SuppressWarnings("unchecked")
    static <S, T> Converter<S, T> identity() {
        return (context, source) -> (T) source;
    }
}
