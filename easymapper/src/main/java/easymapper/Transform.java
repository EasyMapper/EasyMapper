package easymapper;

import java.lang.reflect.Type;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Transform {

    private final Function<Type, Boolean> sourceTypePredicate;
    private final Function<Type, Boolean> destinationTypePredicate;
    private final BiFunction<Object, ConversionContext, Object> function;

    Transform(
        Function<Type, Boolean> sourceTypePredicate,
        Function<Type, Boolean> destinationTypePredicate,
        BiFunction<Object, ConversionContext, Object> function
    ) {
        this.sourceTypePredicate = sourceTypePredicate;
        this.destinationTypePredicate = destinationTypePredicate;
        this.function = function;
    }

    Transform(
        Function<Type, Boolean> sourceTypePredicate,
        Function<Type, Boolean> destinationTypePredicate,
        Function<Object, Object> function
    ) {
        this.sourceTypePredicate = sourceTypePredicate;
        this.destinationTypePredicate = destinationTypePredicate;
        this.function = (source, context) -> function.apply(source);
    }

    static <S, D> Transform create(
        Class<S> sourceType,
        Class<D> destinationType,
        BiFunction<S, ConversionContext, D> function
    ) {
        return new Transform(
            type -> type.equals(sourceType),
            type -> type.equals(destinationType),
            (source, context) -> function.apply(sourceType.cast(source), context));
    }

    public boolean matchSourceType(Type sourceType) {
        return sourceTypePredicate.apply(sourceType);
    }

    public boolean matchDestinationType(Type destinationType) {
        return destinationTypePredicate.apply(destinationType);
    }

    public Object convert(
        Object source,
        ConversionContext context
    ) {
        return function.apply(source, context);
    }
}
