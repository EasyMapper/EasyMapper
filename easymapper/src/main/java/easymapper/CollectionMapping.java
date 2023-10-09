package easymapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class CollectionMapping {

    public static void configure(MapperConfiguration config) {
        config.map(
            CollectionMapping::isIterable,
            CollectionMapping::isIterable,
            mapping -> mapping
                .convert(source -> context -> convert(source, context))
                .project((source, target) -> context -> {}));
    }

    private static boolean isIterable(Type type) {
        if (type instanceof ParameterizedType) {
            return isIterable((ParameterizedType) type);
        } else if (type instanceof Class<?>) {
            return isIterable((Class<?>) type);
        } else {
            return false;
        }
    }

    private static boolean isIterable(ParameterizedType type) {
        return isIterable(type.getRawType());
    }

    private static boolean isIterable(Class<?> type) {
        if (type.equals(Iterable.class)) {
            return true;
        }

        Class<?>[] interfaces = type.getInterfaces();
        for (Class<?> interfaceType : interfaces) {
            if (isIterable(interfaceType)) {
                return true;
            }
        }

        return false;
    }

    private static Object convert(Object source, MappingContext context) {
        return source == null ? null : convert((Iterable<?>) source, context);
    }

    private static Object convert(Iterable<?> source, MappingContext context) {
        return convert(
            source,
            context.getMapper(),
            resolveElementType(context.getSourceType()),
            resolveElementType(context.getDestinationType()));
    }

    private static List<?> convert(
        Iterable<?> source,
        Mapper mapper,
        Type sourceElementType,
        Type destinationElementType
    ) {
        List<?> destination = new ArrayList<>();

        for (Object item : source) {
            destination.add(mapper.map(
                item,
                sourceElementType,
                destinationElementType));
        }

        return destination;
    }

    private static Type resolveElementType(Type destinationType) {
        if (destinationType instanceof ParameterizedType) {
            return resolveElementType((ParameterizedType) destinationType);
        } else {
            String message = "Cannot resolve element type from the type: " + destinationType;
            throw new RuntimeException(message);
        }
    }

    private static Type resolveElementType(ParameterizedType destinationType) {
        return destinationType.getActualTypeArguments()[0];
    }
}
