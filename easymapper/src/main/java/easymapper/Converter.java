package easymapper;

import java.util.function.Supplier;

@FunctionalInterface
public interface Converter<S, T> {

    T convert(MappingContext context, S source);

    default Supplier<T> bind(MappingContext context, S source) {
        return () -> convert(context, source);
    }
}
