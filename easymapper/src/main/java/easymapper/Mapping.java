package easymapper;

import java.lang.reflect.Type;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

class Mapping<S, D> {

    private final Function<Type, Boolean> sourceTypePredicate;
    private final Function<Type, Boolean> destinationTypePredicate;
    private final Function<S, Function<MappingContext, D>> convert;
    private final BiFunction<S, D, Consumer<MappingContext>> project;

    public Mapping(
        Function<Type, Boolean> sourceTypePredicate,
        Function<Type, Boolean> destinationTypePredicate,
        Function<S, Function<MappingContext, D>> convert,
        BiFunction<S, D, Consumer<MappingContext>> project
    ) {
        this.sourceTypePredicate = sourceTypePredicate;
        this.destinationTypePredicate = destinationTypePredicate;
        this.convert = convert;
        this.project = project;
    }

    public boolean matchSourceType(Type sourceType) {
        return sourceTypePredicate.apply(sourceType);
    }

    public boolean matchDestinationType(Type destinationType) {
        return destinationTypePredicate.apply(destinationType);
    }

    public D convert(S source, MappingContext context) {
        return convert.apply(source).apply(context);
    }

    public void project(S source, D destination, MappingContext context) {
        project.apply(source, destination).accept(context);
    }
}
