package easymapper;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static easymapper.Exceptions.argumentNullException;

public final class MappingBuilder<S, D> {

    private final Function<Type, Boolean> sourceTypePredicate;
    private final Function<Type, Boolean> destinationTypePredicate;
    private Function<MappingContext, Function<S, D>> conversion = null;
    private Function<MappingContext, BiConsumer<S, D>> projection = null;
    private final Map<String, Function<MappingContext, Function<S, Object>>> computation = new HashMap<>();

    MappingBuilder(
        Function<Type, Boolean> sourceTypePredicate,
        Function<Type, Boolean> destinationTypePredicate
    ) {
        this.sourceTypePredicate = sourceTypePredicate;
        this.destinationTypePredicate = destinationTypePredicate;
    }

    public MappingBuilder<S, D> convert(
        Function<MappingContext, Function<S, D>> function
    ) {
        if (function == null) {
            throw argumentNullException("function");
        }

        if (conversion != null) {
            String message = "MappingBuilder.convert() can be called only once.";
            throw new IllegalStateException(message);
        }

        conversion = function;

        return this;
    }

    public MappingBuilder<S, D> project(
        Function<MappingContext, BiConsumer<S, D>> action
    ) {
        if (action == null) {
            throw argumentNullException("action");
        }

        if (projection != null) {
            String message = "MappingBuilder.project() can be called only once.";
            throw new IllegalStateException(message);
        }

        projection = action;

        return this;
    }

    public MappingBuilder<S, D> compute(
        String destinationPropertyName,
        Function<MappingContext, Function<S, Object>> function
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
