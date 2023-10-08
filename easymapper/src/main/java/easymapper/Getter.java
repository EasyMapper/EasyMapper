package easymapper;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static easymapper.CamelCase.camelize;

class Getter {

    public static Map<String, Method> getStatedGetters(Type type) {
        if (type instanceof Class<?>) {
            return getStatedGetters((Class<?>) type);
        } else if (type instanceof ParameterizedType) {
            return getStatedGetters((ParameterizedType) type);
        } else {
            String message = "Cannot provide stated getters for the type: " + type;
            throw new RuntimeException(message);
        }
    }

    private static Map<String, Method> getStatedGetters(Class<?> type) {
        Map<String, Method> getters = new HashMap<>();
        for (Method method : type.getMethods()) {
            if (method.getParameterCount() > 0 ||
                method.getDeclaringClass().equals(Object.class)) {
                continue;
            }
            String methodName = method.getName();
            if (methodName.startsWith("get")) {
                getters.put(camelize(methodName.substring(3)), method);
            } else if (methodName.startsWith("is")) {
                getters.put(camelize(methodName.substring(2)), method);
            } else {
                getters.put(methodName, method);
            }
        }
        return getters;
    }

    private static Map<String, Method> getStatedGetters(ParameterizedType type) {
        if (type.getRawType() instanceof Class<?>) {
            return getStatedGetters((Class<?>) type.getRawType());
        } else {
            String message = "Cannot provide stated getters for the type: " + type;
            throw new RuntimeException(message);
        }
    }
}
