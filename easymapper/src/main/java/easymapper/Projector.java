package easymapper;

import java.lang.reflect.Type;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class Projector {

    private final Function<Type, Boolean> sourceTypePredicate;
    private final Function<Type, Boolean> destinationTypePredicate;
    private final BiFunction<Object, Object, Consumer<ProjectionContext>> consumer;

    private Projector(
        Function<Type, Boolean> sourceTypePredicate,
        Function<Type, Boolean> destinationTypePredicate,
        BiFunction<Object, Object, Consumer<ProjectionContext>> consumer
    ) {
        this.sourceTypePredicate = sourceTypePredicate;
        this.destinationTypePredicate = destinationTypePredicate;
        this.consumer = consumer;
    }

    @SuppressWarnings("unchecked")
    static <D, S> Projector create(
        Function<Type, Boolean> sourceTypePredicate,
        Function<Type, Boolean> destinationTypePredicate,
        BiFunction<S, D, Consumer<ProjectionContext>> consumer
    ) {
        return new Projector(
            sourceTypePredicate,
            destinationTypePredicate,
            (source, destination) -> context -> consumer
                .apply((S) source, (D) destination)
                .accept(context));
    }

    public boolean match(Type sourceType, Type destinationType) {
        return sourceTypePredicate.apply(sourceType)
            && destinationTypePredicate.apply(destinationType);
    }

    public void project(
        Object source,
        Object destination,
        ProjectionContext context
    ) {
        Consumer<ProjectionContext> apply = consumer.apply(source, destination);
        apply.accept(context);
    }
}
