package easymapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class CollectionMapping {

    public static void configurer(MapperConfiguration config) {
        config
            .addConverter(
                CollectionMapping::matchSourceType,
                CollectionMapping::matchDestinationType,
                source -> context -> convert(source, context))
            .addProjector(
                CollectionMapping::matchSourceType,
                CollectionMapping::matchDestinationType,
                (source, target) -> context -> {});
    }

    private static boolean matchSourceType(Type type) {
        return isIterable(type);
    }

    private static boolean matchDestinationType(Type type) {
        return isIterable(type);
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
        Type rawType = type.getRawType();
        return isIterable(rawType);
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

    private static Object convert(
        Object source,
        ConversionContext context
    ) {
        return source == null ? null : convert((Iterable<?>) source, context);
    }

    private static Object convert(
        Iterable<?> source,
        ConversionContext context
    ) {
        Mapper mapper = context.getMapper();

        Type sourceElementType = getElementType(context.getSourceType());
        Type destinationElementType = getElementType(context.getDestinationType());

        List<?> destination = new ArrayList<>();
        for (Object item : source) {
            destination.add(mapper.map(item, sourceElementType, destinationElementType));
        }

        return destination;
    }

    private static Type getElementType(Type destinationType) {
        if (destinationType instanceof ParameterizedType) {
            return getElementType((ParameterizedType) destinationType);
        } else {
            String message = "Cannot get element type from the type: " + destinationType;
            throw new RuntimeException(message);
        }
    }

    private static Type getElementType(ParameterizedType destinationType) {
        return destinationType.getActualTypeArguments()[0];
    }
}
