package easymapper;

import java.util.ArrayList;
import java.util.List;

import static easymapper.Collections.resolveElementType;

class CollectionMapping {

    public static void configure(MapperConfiguration config) {
        TypePredicate isIterable = Collections::isIterable;
        config.map(isIterable, isIterable, mapping -> mapping
            .convert(CollectionMapping::convert)
            .project(Projection.empty())
        );
    }

    private static Object convert(MappingContext context, Object source) {
        return source == null ? null : convert(context, (Iterable<?>) source);
    }

    private static Object convert(MappingContext context, Iterable<?> source) {
        MappingContext elementMappingContext = context.branch(
            resolveElementType(context.getSourceType()),
            resolveElementType(context.getDestinationType()));

        List<Object> destination = new ArrayList<>();
        for (Object item : source) {
            destination.add(elementMappingContext.convert(item));
        }

        return destination;
    }
}
