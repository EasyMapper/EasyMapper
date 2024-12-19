package easymapper;

import java.util.ArrayList;
import java.util.List;

import static easymapper.Collections.resolveElementType;

class CollectionMapping {

    public static void configure(MapperConfiguration config) {
        TypePredicate isIterable = Collections::isIterable;
        config.addConverter(isIterable, isIterable, CollectionMapping::convert);
        config.addProjector(isIterable, isIterable, Projector.empty());
    }

    private static Object convert(MappingContext context, Object source) {
        return source == null ? null : convert(context, (Iterable<?>) source);
    }

    private static Object convert(MappingContext context, Iterable<?> source) {
        MappingContext elementMappingContext = context.branch(
            resolveElementType(context.getSourceType()),
            resolveElementType(context.getTargetType()));

        List<Object> target = new ArrayList<>();
        for (Object item : source) {
            target.add(elementMappingContext.convert(item));
        }

        return target;
    }
}
