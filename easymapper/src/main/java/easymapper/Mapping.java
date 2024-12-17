package easymapper;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class Mapping<S, D> {

    public static final Mapping<Object, Object> EMPTY = emptyMapping();

    private static Mapping<Object, Object> emptyMapping() {
        TypePredicate acceptAll = TypePredicate.ACCEPT_ALL_TYPES;
        return new Mapping<>(acceptAll, acceptAll, null, null, emptyMap());
    }

    private static Map<String, Computation<Object>> emptyMap() {
        return Collections.unmodifiableMap(new HashMap<>());
    }

    private final TypePredicate sourceTypePredicate;
    private final TypePredicate destinationTypePredicate;
    private final Conversion<S, D> conversion;
    private final Projection<S, D> projection;
    private final Map<String, Computation<S>> computation;

    public Mapping(
        TypePredicate sourceTypePredicate,
        TypePredicate destinationTypePredicate,
        Conversion<S, D> conversion,
        Projection<S, D> projection,
        Map<String, Computation<S>> computation
    ) {
        this.sourceTypePredicate = sourceTypePredicate;
        this.destinationTypePredicate = destinationTypePredicate;
        this.conversion = conversion;
        this.projection = projection;
        this.computation = computation;
    }

    public boolean match(Type sourceType, Type destinationType) {
        return sourceTypePredicate.test(sourceType)
            && destinationTypePredicate.test(destinationType);
    }

    public Optional<Conversion<S, D>> conversion() {
        return Optional.ofNullable(conversion);
    }

    public Optional<Projection<S, D>> projection() {
        return Optional.ofNullable(projection);
    }

    public Optional<Computation<S>> computation(
        String destinationPropertyName
    ) {
        return Optional.ofNullable(computation.get(destinationPropertyName));
    }
}
