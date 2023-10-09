package easymapper;

import java.lang.reflect.Type;
import java.util.function.Function;

class Mapping<S, D> {

    private final Function<Type, Boolean> sourceTypePredicate;
    private final Function<Type, Boolean> destinationTypePredicate;
    private final Function<S, Function<MappingContext, D>> convertFunction;

    public Mapping(
        Function<Type, Boolean> sourceTypePredicate,
        Function<Type, Boolean> destinationTypePredicate,
        Function<S, Function<MappingContext, D>> convertFunction
    ) {
        this.sourceTypePredicate = sourceTypePredicate;
        this.destinationTypePredicate = destinationTypePredicate;
        this.convertFunction = convertFunction;
    }

    public D convert(S source, MappingContext context) {
        return convertFunction.apply(source).apply(context);
    }

    public boolean matchSourceType(Type sourceType) {
        return sourceTypePredicate.apply(sourceType);
    }

    public boolean matchDestinationType(Type destinationType) {
        return destinationTypePredicate.apply(destinationType);
    }
}
