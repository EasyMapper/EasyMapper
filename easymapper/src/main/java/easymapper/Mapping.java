package easymapper;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

class Mapping<S, D> {

    private final Function<Type, Boolean> sourceTypePredicate;
    private final Function<Type, Boolean> destinationTypePredicate;
    private final Function<S, Function<MappingContext, D>> conversion;
    private final BiFunction<S, D, Consumer<MappingContext>> projection;
    private final Map<String, Function<S, Function<MappingContext, Object>>> computation;

    public Mapping(
        Function<Type, Boolean> sourceTypePredicate,
        Function<Type, Boolean> destinationTypePredicate,
        Function<S, Function<MappingContext, D>> conversion,
        BiFunction<S, D, Consumer<MappingContext>> projection,
        Map<String, Function<S, Function<MappingContext, Object>>> computation
    ) {
        this.sourceTypePredicate = sourceTypePredicate;
        this.destinationTypePredicate = destinationTypePredicate;
        this.conversion = conversion;
        this.projection = projection;
        this.computation = computation;
    }

    public boolean matchSourceType(Type sourceType) {
        return sourceTypePredicate.apply(sourceType);
    }

    public boolean matchDestinationType(Type destinationType) {
        return destinationTypePredicate.apply(destinationType);
    }

    public boolean match(Type sourceType, Type destinationType) {
        return sourceTypePredicate.apply(sourceType)
            && destinationTypePredicate.apply(destinationType);
    }

    public boolean hasConversion() {
        return conversion != null;
    }

    public D convert(S source, MappingContext context) {
        return conversion.apply(source).apply(context);
    }

    public boolean hasProjection() {
        return projection != null;
    }

    public void project(S source, D destination, MappingContext context) {
        projection.apply(source, destination).accept(context);
    }

    public Optional<Object> compute(
        String destinationPropertyName,
        S source,
        MappingContext context
    ) {
        return Optional
            .ofNullable(computation.get(destinationPropertyName))
            .map(compute -> compute.apply(source).apply(context));
    }
}
