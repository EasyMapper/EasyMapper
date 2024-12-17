package easymapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.reverse;

class Collections {

    public static boolean isIterable(Type type) {
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

    public static Type resolveElementType(Type destinationType) {
        if (destinationType instanceof ParameterizedType) {
            return resolveElementType((ParameterizedType) destinationType);
        } else {
            throw new RuntimeException(
                "Cannot resolve element type from the type: " + destinationType
            );
        }
    }

    private static Type resolveElementType(ParameterizedType type) {
        return type.getActualTypeArguments()[0];
    }

    public static <T> List<T> copyInReverseOrder(List<T> list) {
        ArrayList<T> copy = new ArrayList<>(list);
        reverse(copy);
        return java.util.Collections.unmodifiableList(copy);
    }
}
