package easymapper;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

class Mapping<S, D> {

    public static final Mapping<Object, Object> empty = new Mapping<>(
        TypePredicates.acceptAllTypes,
        TypePredicates.acceptAllTypes,
        null,
        null,
        Collections.unmodifiableMap(new HashMap<>())
    );

    private final Function<Type, Boolean> sourceTypePredicate;
    private final Function<Type, Boolean> destinationTypePredicate;
    private final Function<MappingContext, Function<S, D>> conversion;
    private final Function<MappingContext, BiConsumer<S, D>> projection;
    private final Map<String, Function<MappingContext, Function<S, Object>>> computation;

    public Mapping(
        Function<Type, Boolean> sourceTypePredicate,
        Function<Type, Boolean> destinationTypePredicate,
        Function<MappingContext, Function<S, D>> conversion,
        Function<MappingContext, BiConsumer<S, D>> projection,
        Map<String, Function<MappingContext, Function<S, Object>>> computation
    ) {
        this.sourceTypePredicate = sourceTypePredicate;
        this.destinationTypePredicate = destinationTypePredicate;
        this.conversion = conversion;
        this.projection = projection;
        this.computation = computation;
    }

    public boolean match(Type sourceType, Type destinationType) {
        return sourceTypePredicate.apply(sourceType)
            && destinationTypePredicate.apply(destinationType);
    }

    public Optional<Function<MappingContext, Function<S, D>>> conversion() {
        return Optional.ofNullable(conversion);
    }

    public Optional<Function<MappingContext, BiConsumer<S, D>>> projection() {
        return Optional.ofNullable(projection);
    }

    public Optional<Function<MappingContext, Function<S, Object>>> computation(
        String destinationPropertyName
    ) {
        return Optional.ofNullable(computation.get(destinationPropertyName));
    }
}
