package easymapper;

import java.util.function.BiConsumer;

@FunctionalInterface
interface ObjectProjector extends Projector<Object, Object> {

    @SuppressWarnings("unchecked")
    static <S, T> ObjectProjector from(Projector<S, T> projector) {
        return (context, source, target) -> projector.project(
            context,
            (S) source,
            (T) target
        );
    }

    default BiConsumer<Object, Object> bindContext(MappingContext context) {
        return (source, target) -> project(context, source, target);
    }
}
