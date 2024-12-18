package easymapper;

import java.util.function.Supplier;

@FunctionalInterface
public interface Conversion<S, D> {

    D convert(MappingContext context, S source);

    static <T> Conversion<T, T> identity() {
        return (context, source) -> (T) source;
    }

    default Supplier<Object> bind(MappingContext context, S source) {
        return () -> convert(context, source);
    }
}
