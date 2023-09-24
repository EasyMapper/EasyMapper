package easymapper;

import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;
import static java.util.stream.Stream.concat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

final class Property {

    private String name;
    private Method getter;
    private Method setter;

    private Property(String name, Method getter, Method setter) {
        this.name = name;
        this.getter = getter;
        this.setter = setter;
    }

    public static Map<String, Property> getProperties(Class<?> type) {
        Map<String, Method> getters = getGetters(type);
        Map<String, Method> setters = getSetters(type);

        return concat(getters.keySet().stream(), setters.keySet().stream())
            .distinct()
            .map(name -> new Property(
                name,
                getters.getOrDefault(name, null),
                setters.getOrDefault(name, null)))
            .collect(Collectors.toMap(x -> x.getName(), x -> x));
    }

    private static Map<String, Method> getGetters(Class<?> type) {
        Map<String, Method> getters = new HashMap<>();
        for (Method method : type.getMethods()) {
            String methodName = method.getName();
            if (methodName.startsWith("get") == false) {
                continue;
            }
            String propertyName = camelize(methodName.substring(3));
            getters.put(propertyName, method);
        }
        return getters;
    }

    private static Map<String, Method> getSetters(Class<?> type) {
        Map<String, Method> setters = new HashMap<>();
        for (Method method : type.getMethods()) {
            String methodName = method.getName();
            if (methodName.startsWith("set") == false) {
                continue;
            }
            String propertyName = camelize(methodName.substring(3));
            setters.put(propertyName, method);
        }
        return setters;
    }

    private static String camelize(String s) {
        char head = s.charAt(0);
        if (isUpperCase(head)) {
            return toLowerCase(head) + s.substring(1);
        } else {
            return s;
        }
    }

    public String getName() {
        return name;
    }

    public Object getValue(Object instance) {
        try {
            return getter.invoke(instance);
        } catch (IllegalAccessException
            | IllegalArgumentException
            | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setValueIfPossible(Object instance, Object value) {
        if (setter == null) {
            return;
        }

        try {
            setter.invoke(instance, value);
        } catch (IllegalAccessException
            | IllegalArgumentException
            | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }
}
