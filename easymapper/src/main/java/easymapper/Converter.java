package easymapper;

import java.lang.reflect.Type;
import java.util.function.Function;

public class Converter {

    private final Function<Type, Boolean> sourceTypePredicate;
    private final Function<Type, Boolean> destinationTypePredicate;
    private final ConverterFunction<Object, Object> function;

    Converter(
        Function<Type, Boolean> sourceTypePredicate,
        Function<Type, Boolean> destinationTypePredicate,
        ConverterFunction<Object, Object> function
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

    @SuppressWarnings("unchecked")
    static <S, D> Converter create(
        Function<Type, Boolean> sourceTypePredicate,
        Function<Type, Boolean> destinationTypePredicate,
        ConverterFunction<S, D> function
    ) {
        return new Converter(
            sourceTypePredicate,
            destinationTypePredicate,
            (source, context) -> function.convert((S) source, context));
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
        return function.convert(source, context);
    }
}
