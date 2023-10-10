package easymapper;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static easymapper.Exceptions.argumentNullException;

public final class MappingBuilder<S, D> {

    private final Function<Type, Boolean> sourceTypePredicate;
    private final Function<Type, Boolean> destinationTypePredicate;
    private Function<S, Function<MappingContext, D>> conversion = null;
    private BiFunction<S, D, Consumer<MappingContext>> projection = null;
    private final Map<String, Function<S, Function<MappingContext, Object>>> computation = new HashMap<>();

    MappingBuilder(
        Function<Type, Boolean> sourceTypePredicate,
        Function<Type, Boolean> destinationTypePredicate
    ) {
        this.sourceTypePredicate = sourceTypePredicate;
        this.destinationTypePredicate = destinationTypePredicate;
    }

    public MappingBuilder<S, D> convert(
        Function<S, Function<MappingContext, D>> function
    ) {
        if (function == null) {
            throw argumentNullException("function");
        }

        if (this.conversion != null) {
            String message = "MappingBuilder.convert() can be called only once.";
            throw new IllegalStateException(message);
        }

        this.conversion = function;

        return this;
    }

    public MappingBuilder<S, D> project(
        BiFunction<S, D, Consumer<MappingContext>> action
    ) {
        if (action == null) {
            throw argumentNullException("action");
        }

        if (this.projection != null) {
            String message = "MappingBuilder.project() can be called only once.";
            throw new IllegalStateException(message);
        }

        this.projection = action;

        return this;
    }

    public MappingBuilder<S, D> compute(
        String destinationPropertyName,
        Function<S, Function<MappingContext, Object>> function
    ) {
        if (destinationPropertyName == null) {
            throw argumentNullException("destinationPropertyName");
        } else if (function == null) {
            throw argumentNullException("function");
        }

        if (computation.containsKey(destinationPropertyName)) {
            String message = String.format(
                "MappingBuilder.compute() can be called only once for the same destination property name. "
                + "Destination property name: %s.",
                destinationPropertyName);
            throw new IllegalStateException(message);
        }

        computation.put(destinationPropertyName, function);

        return this;
    }

    Mapping<S, D> build() {
        return new Mapping<>(
            sourceTypePredicate,
            destinationTypePredicate,
            conversion,
            projection,
            Collections.unmodifiableMap(computation));
    }
}
