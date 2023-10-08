package easymapper;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static easymapper.CamelCase.camelize;

class Setter {

    public static Map<String, Method> getStatedSetters(Type type) {
        if (type instanceof Class<?>) {
            return getStatedSetters((Class<?>) type);
        } else if (type instanceof ParameterizedType) {
            return getStatedSetters((ParameterizedType) type);
        } else {
            String message = "Cannot provide stated setters for the type: " + type;
            throw new RuntimeException(message);
        }
    }

    private static Map<String, Method> getStatedSetters(Class<?> type) {
        Map<String, Method> setters = new HashMap<>();
        for (Method method : type.getMethods()) {
            if (method.getParameterCount() != 1) {
                continue;
            }
            String methodName = method.getName();
            if (methodName.startsWith("set")) {
                setters.put(camelize(methodName.substring(3)), method);
            }
        }
        return setters;
    }

    private static Map<String, Method> getStatedSetters(ParameterizedType type) {
        if (type.getRawType() instanceof Class<?>) {
            return getStatedSetters((Class<?>) type.getRawType());
        } else {
            String message = "Cannot provide stated setters for the type: " + type;
            throw new RuntimeException(message);
        }
    }
}
