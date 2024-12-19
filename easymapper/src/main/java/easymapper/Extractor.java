package easymapper;

import java.util.function.Supplier;

@FunctionalInterface
public interface Extractor<S, P> {

    P extract(MappingContext context, S source);

    default Supplier<P> bind(MappingContext context, S source) {
        return () -> extract(context, source);
    }
}
