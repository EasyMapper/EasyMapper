package easymapper;

import java.lang.reflect.Type;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Converter {

    private final Function<Type, Boolean> sourceTypePredicate;
    private final Function<Type, Boolean> destinationTypePredicate;
    private final BiFunction<Object, ConversionContext, Object> function;

    private Converter(
        Function<Type, Boolean> sourceTypePredicate,
        Function<Type, Boolean> destinationTypePredicate,
        BiFunction<Object, ConversionContext, Object> function
    ) {
        this.sourceTypePredicate = sourceTypePredicate;
        this.destinationTypePredicate = destinationTypePredicate;
        this.function = function;
    }

    @SuppressWarnings("unchecked")
    static <S, D> Converter create(
        Function<Type, Boolean> sourceTypePredicate,
        Function<Type, Boolean> destinationTypePredicate,
        BiFunction<S, ConversionContext, D> function
    ) {
        return new Converter(
            sourceTypePredicate,
            destinationTypePredicate,
            (source, context) -> function.apply((S) source, context));
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
