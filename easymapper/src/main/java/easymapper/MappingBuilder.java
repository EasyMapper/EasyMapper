package easymapper;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public final class MappingBuilder<S, D> {

    public MappingBuilder<S, D> convert(
        Function<S, Function<MappingContext, D>> convert
    ) {
        return null;
    }

    public MappingBuilder<S, D> project(
        BiFunction<S, D, Consumer<MappingContext>> project
    ) {
        return null;
    }

    public MappingBuilder<S, D> compute(
        String destinationPropertyName,
        Function<S, Function<MappingContext, Object>> compute
    ) {
        return null;
    }
}
