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
                .convert(context -> source -> convert(context, source))
                .project(context -> (source, target) -> {}));
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

    private static Object convert(MappingContext context, Object source) {
        return source == null ? null : convert(context, (Iterable<?>) source);
    }

    private static Object convert(MappingContext context, Iterable<?> source) {
        return convert(
            context.getMapper(),
            resolveElementType(context.getSourceType()),
            resolveElementType(context.getDestinationType()),
            source);
    }

    private static List<?> convert(
        Mapper mapper,
        Type sourceElementType,
        Type destinationElementType,
        Iterable<?> source
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
