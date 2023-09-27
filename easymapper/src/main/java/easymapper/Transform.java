package easymapper;

import java.util.function.Function;

public class Transform {

    private final Class<?> sourceType;
    private final Class<?> destinationType;
    private final Function<Object, Object> function;

    Transform(
        Class<?> sourceType,
        Class<?> destinationType,
        Function<Object, Object> function
    ) {
        this.sourceType = sourceType;
        this.destinationType = destinationType;
        this.function = function;
    }

    static <S, D> Transform create(
        Class<S> sourceType,
        Class<D> destinationType,
        Function<S, D> function
    ) {
        return new Transform(
            sourceType,
            destinationType,
            x -> function.apply(sourceType.cast(x)));
    }

    public Class<?> getSourceType() {
        return sourceType;
    }

    public Class<?> getDestinationType() {
        return destinationType;
    }

    public Object transform(Object source) {
        return function.apply(source);
    }
}
