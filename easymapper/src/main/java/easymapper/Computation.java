package easymapper;

import java.util.function.Supplier;

@FunctionalInterface
public interface Computation<S> {

    Object compute(MappingContext context, S source);

    default Supplier<Object> bind(MappingContext context, S source) {
        return () -> compute(context, source);
    }
}
