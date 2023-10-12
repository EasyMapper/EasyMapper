package easymapper;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
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
    private final BiFunction<S, D, Consumer<MappingContext>> projection;
    private final Map<String, Function<S, Function<MappingContext, Object>>> computation;

    public Mapping(
        Function<Type, Boolean> sourceTypePredicate,
        Function<Type, Boolean> destinationTypePredicate,
        Function<MappingContext, Function<S, D>> conversion,
        BiFunction<S, D, Consumer<MappingContext>> projection,
        Map<String, Function<S, Function<MappingContext, Object>>> computation
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

    public Optional<BiFunction<S, D, Consumer<MappingContext>>> projection() {
        return Optional.ofNullable(projection);
    }

    public Optional<Function<S, Function<MappingContext, Object>>> computation(
        String destinationPropertyName
    ) {
        return Optional.ofNullable(computation.get(destinationPropertyName));
    }
}
