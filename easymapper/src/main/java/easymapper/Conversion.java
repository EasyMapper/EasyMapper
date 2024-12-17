package easymapper;

@FunctionalInterface
public interface Conversion<S, D> {

    D convert(MappingContext context, S source);

    static <T> Conversion<T, T> identity() {
        return (context, source) -> (T) source;
    }
}
