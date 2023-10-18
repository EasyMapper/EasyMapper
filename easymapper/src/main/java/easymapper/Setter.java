package easymapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static easymapper.CamelCase.camelize;

class Setter {

    private final Type type;
    private final String name;
    private final BiConsumer<Object, Object> operation;

    public Setter(
        Type type,
        String name,
        BiConsumer<Object, Object> operation
    ) {
        this.type = type;
        this.name = name;
        this.operation = operation;
    }

    private static Setter create(Method method) {
        return new Setter(
            method.getGenericParameterTypes()[0],
            method.getName(),
            (instance, value) -> {
                try {
                    method.invoke(instance, value);
                } catch (IllegalAccessException |
                     InvocationTargetException exception) {
                    throw new RuntimeException(exception);
                }
            });
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
        } else {
            String message = "Cannot provide stated setters for the type: " + type;
            throw new RuntimeException(message);
        }
    }

    private static Map<String, Setter> getStatedSetters(Class<?> type) {
        Map<String, Setter> setters = new HashMap<>();
        for (Method method : type.getMethods()) {
            if (method.getParameterCount() != 1) {
                continue;
            }
            String methodName = method.getName();
            if (methodName.startsWith("set")) {
                setters.put(camelize(methodName.substring(3)), create(method));
            }
        }
        return setters;
    }

    private static Map<String, Setter> getStatedSetters(ParameterizedType type) {
        if (type.getRawType() instanceof Class<?>) {
            return getStatedSetters((Class<?>) type.getRawType());
        } else {
            String message = "Cannot provide stated setters for the type: " + type;
            throw new RuntimeException(message);
        }
    }
}
