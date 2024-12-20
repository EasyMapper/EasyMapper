package easymapper;

import java.util.ArrayList;
import java.util.List;

import static easymapper.Collections.resolveElementType;

class CollectionMapping {

    public static void configure(MapperConfigurationBuilder config) {
        TypePredicate isIterable = Collections::isIterable;
        config.addConverter(isIterable, isIterable, CollectionMapping::convert);
        config.addProjector(isIterable, isIterable, Projector.empty());
    }

    private static Object convert(Object source, MappingContext context) {
        return source == null ? null : convert((Iterable<?>) source, context);
    }

    private static Object convert(Iterable<?> source, MappingContext context) {
        MappingContext elementMappingContext = context.branch(
            resolveElementType(context.getSourceType()),
            resolveElementType(context.getTargetType()));

        List<Object> list = new ArrayList<>();
        for (Object element : source) {
            list.add(elementMappingContext.convert(element));
        }

        return list;
    }
}
