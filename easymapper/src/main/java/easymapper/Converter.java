package easymapper;

import java.lang.reflect.Type;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Converter {

    private final Function<Type, Boolean> sourceTypePredicate;
    private final Function<Type, Boolean> destinationTypePredicate;
    private final BiFunction<Object, ConversionContext, Object> function;

    Converter(
        Function<Type, Boolean> sourceTypePredicate,
        Function<Type, Boolean> destinationTypePredicate,
        BiFunction<Object, ConversionContext, Object> function
    ) {
        this.sourceTypePredicate = sourceTypePredicate;
        this.destinationTypePredicate = destinationTypePredicate;
        this.function = function;
    }

    Converter(
        Function<Type, Boolean> sourceTypePredicate,
        Function<Type, Boolean> destinationTypePredicate,
        Function<Object, Object> function
    ) {
        this.sourceTypePredicate = sourceTypePredicate;
        this.destinationTypePredicate = destinationTypePredicate;
        this.function = (source, context) -> function.apply(source);
    }

    static <S, D> Converter create(
        Class<S> sourceType,
        Class<D> destinationType,
        BiFunction<S, ConversionContext, D> function
    ) {
        return new Converter(
            type -> type.equals(sourceType),
            type -> type.equals(destinationType),
            (source, context) -> function.apply(sourceType.cast(source), context));
    }

    public boolean match(
        Type sourceType,
        Type destinationType
    ) {
        return sourceTypePredicate.apply(sourceType)
            && destinationTypePredicate.apply(destinationType);
    }

    public Object convert(
        Object source,
        ConversionContext context
    ) {
        return function.apply(source, context);
    }
}
