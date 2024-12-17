package easymapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import lombok.AllArgsConstructor;

import static easymapper.CamelCase.camelize;

@AllArgsConstructor
class Getter {

    private final Type type;
    private final String name;
    private final Function<Object, Object> function;

    private static Getter create(Method method) {
        return new Getter(
            method.getGenericReturnType(),
            method.getName(),
            instance -> {
                try {
                    return method.invoke(instance);
                } catch (IllegalAccessException
                     | IllegalArgumentException
                     | InvocationTargetException exception) {
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

    public Object invoke(Object instance) {
        return function.apply(instance);
    }

    public static Map<String, Getter> getStatedGetters(Type type) {
        if (type instanceof Class<?>) {
            return getStatedGetters((Class<?>) type);
        } else if (type instanceof ParameterizedType) {
            return getStatedGetters((ParameterizedType) type);
        } else if (type instanceof TupleType) {
            return getStatedGetters(((TupleType) type));
        } else {
            throw new RuntimeException(
                "Cannot provide stated getters for the type: " + type
            );
        }
    }

    private static Map<String, Getter> getStatedGetters(Class<?> type) {
        Map<String, Getter> getters = new HashMap<>();

        for (Method method : type.getMethods()) {
            if (method.getParameterCount() > 0 ||
                method.getDeclaringClass().equals(Object.class)) {
                continue;
            }

            Getter getter = create(method);

            String methodName = method.getName();
            if (methodName.startsWith("get")) {
                getters.put(camelize(methodName.substring(3)), getter);
            } else if (methodName.startsWith("is")) {
                getters.put(camelize(methodName.substring(2)), getter);
            } else {
                getters.put(methodName, getter);
            }
        }

        return getters;
    }

    private static Map<String, Getter> getStatedGetters(
        ParameterizedType type
    ) {
        if (type.getRawType() instanceof Class<?>) {
            return getStatedGetters((Class<?>) type.getRawType());
        } else {
            throw new RuntimeException(
                "Cannot provide stated getters for the type: " + type
            );
        }
    }

    private static Map<String, Getter> getStatedGetters(TupleType type) {
        return type.getGetters();
    }
}
