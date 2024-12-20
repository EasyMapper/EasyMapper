package easymapper;

import java.util.function.BiConsumer;

@FunctionalInterface
interface ObjectProjector extends Projector<Object, Object> {

    @SuppressWarnings("unchecked")
    static <S, T> ObjectProjector from(Projector<S, T> projector) {
        return (source, target, context) -> projector.project(
            (S) source, (T) target, context
        );
    }

    default BiConsumer<Object, Object> bindContext(MappingContext context) {
        return (source, target) -> project(source, target, context);
    }
}
