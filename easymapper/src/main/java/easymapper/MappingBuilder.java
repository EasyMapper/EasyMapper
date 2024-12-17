package easymapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lombok.NonNull;

public final class MappingBuilder<S, D> {

    private final TypePredicate sourceTypePredicate;
    private final TypePredicate destinationTypePredicate;
    private Conversion<S, D> conversion = null;
    private Projection<S, D> projection = null;
    private final Map<String, Computation<S>> computations = new HashMap<>();

    MappingBuilder(
        TypePredicate sourceTypePredicate,
        TypePredicate destinationTypePredicate
    ) {
        this.sourceTypePredicate = sourceTypePredicate;
        this.destinationTypePredicate = destinationTypePredicate;
    }

    public MappingBuilder<S, D> convert(@NonNull Conversion<S, D> function) {
        if (conversion != null) {
            String message = "MappingBuilder.convert() can be called only once.";
            throw new IllegalStateException(message);
        }

        conversion = function;
        return this;
    }

    public MappingBuilder<S, D> project(@NonNull Projection<S, D> action) {
        if (projection != null) {
            String message = "MappingBuilder.project() can be called only once.";
            throw new IllegalStateException(message);
        }

        projection = action;
        return this;
    }

    public MappingBuilder<S, D> compute(
        @NonNull String destinationPropertyName,
        @NonNull Computation<S> function
    ) {
        if (computations.containsKey(destinationPropertyName)) {
            String message = String.format(
                "MappingBuilder.compute() can be called only once for"
                    + " the same destination property name."
                    + " Destination property name: %s.",
                destinationPropertyName);
            throw new IllegalStateException(message);
        }

        computations.put(destinationPropertyName, function);
        return this;
    }

    Mapping<S, D> build() {
        return new Mapping<>(
            sourceTypePredicate,
            destinationTypePredicate,
            conversion,
            projection,
            Collections.unmodifiableMap(computations));
    }
}
