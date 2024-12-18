package easymapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import lombok.AllArgsConstructor;

import static easymapper.CamelCase.camelize;

@AllArgsConstructor
class Setter {

    private final Type type;
    private final String name;
    private final BiConsumer<Object, Object> operation;

    private static Setter create(Method method) {
        return new Setter(
            method.getGenericParameterTypes()[0],
            method.getName(),
            (instance, value) -> invoke(method, instance, value)
        );
    }

    private static void invoke(Method method, Object instance, Object arg) {
        try {
            method.invoke(instance, arg);
        } catch (IllegalAccessException |
                 InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }

    public Type type() {
        return type;
    }

    public String name() {
        return name;
    }

    public void invoke(Object instance, Object value) {
        operation.accept(instance, value);
    }

    public static Map<String, Setter> getStatedSetters(Type type) {
        if (type instanceof Class<?>) {
            return getStatedSetters((Class<?>) type);
        } else if (type instanceof ParameterizedType) {
            return getStatedSetters((ParameterizedType) type);
        } else if (type instanceof TupleType) {
            return new HashMap<>();
        } else {
            throw new RuntimeException(
                "Cannot provide stated setters for the type: " + type
            );
        }
    }

    private static Map<String, Setter> getStatedSetters(Class<?> type) {
        Map<String, Setter> setters = new HashMap<>();

        for (Method method : type.getMethods()) {
            if (method.getParameterCount() != 1) {
                continue;
            }

            String methodName = method.getName();
            String propertyName = methodName.startsWith("set")
                ? camelize(methodName.substring(3))
                : methodName;
            setters.put(propertyName, create(method));
        }

        return setters;
    }

    private static Map<String, Setter> getStatedSetters(ParameterizedType type) {
        if (type.getRawType() instanceof Class<?>) {
            return getStatedSetters((Class<?>) type.getRawType());
        } else {
            throw new RuntimeException(
                "Cannot provide stated setters for the type: " + type
            );
        }
    }
}
