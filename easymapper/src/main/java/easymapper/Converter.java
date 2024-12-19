package easymapper;

import java.util.function.Supplier;

@FunctionalInterface
public interface Converter<S, D> {

    D convert(MappingContext context, S source);

    default Supplier<D> bind(MappingContext context, S source) {
        return () -> convert(context, source);
    }
}
