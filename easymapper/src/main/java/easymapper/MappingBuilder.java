package easymapper;

import java.lang.reflect.Type;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static easymapper.Exceptions.argumentNullException;

public final class MappingBuilder<S, D> {

    private final Function<Type, Boolean> sourceTypePredicate;
    private final Function<Type, Boolean> destinationTypePredicate;
    private Function<S, Function<MappingContext, D>> convertFunction = null;

    MappingBuilder(
        Function<Type, Boolean> sourceTypePredicate,
        Function<Type, Boolean> destinationTypePredicate
    ) {
        this.sourceTypePredicate = sourceTypePredicate;
        this.destinationTypePredicate = destinationTypePredicate;
    }

    public MappingBuilder<S, D> convert(
        Function<S, Function<MappingContext, D>> function
    ) {
        if (function == null) {
            throw argumentNullException("function");
        }

        if (this.convertFunction != null) {
            String message = "MappingBuilder.convert() can be called only once.";
            throw new IllegalStateException(message);
        }

        this.convertFunction = function;

        return this;
    }

    public MappingBuilder<S, D> project(
        BiFunction<S, D, Consumer<MappingContext>> project
    ) {
        return null;
    }

    public MappingBuilder<S, D> compute(
        String destinationPropertyName,
        Function<S, Function<MappingContext, Object>> compute
    ) {
        return null;
    }

    Mapping<S, D> build() {
        return new Mapping<>(
            sourceTypePredicate,
            destinationTypePredicate,
            convertFunction);
    }
}
