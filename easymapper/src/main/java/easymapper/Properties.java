package easymapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

class Properties {

    private final Class<?> sourceType;
    private final Map<String, Property> declaredProperties;

    private Properties(
        Class<?> sourceType,
        Map<String, Property> declaredProperties
    ) {
        this.sourceType = sourceType;
        this.declaredProperties = declaredProperties;
    }

    public static Properties get(Class<?> sourceType) {
        return new Properties(sourceType, getDeclaredProperties(sourceType));
    }

    private static Map<String, Property> getDeclaredProperties(Class<?> type) {
        Map<String, Method> declaredGetters = getDeclaredGetters(type);
        Map<String, Method> declaredSetters = getDeclaredSetters(type);

        return declaredGetters
            .keySet()
            .stream()
            .distinct()
            .map(name -> {
                Method declaredGetter = declaredGetters.get(name);
                Method declaredSetter = declaredSetters.getOrDefault(name, null);
                Class<?> propertyType = declaredGetter == null
                    ? declaredSetter.getParameterTypes()[0]
                    : declaredGetter.getReturnType();
                return new Property(
                    propertyType,
                    name,
                    getGetter(declaredGetter),
                    getSetter(declaredSetter));
            })
            .collect(toMap(Property::getName, identity()));
    }

    private static Map<String, Method> getDeclaredGetters(Class<?> type) {
        Map<String, Method> getters = new HashMap<>();
        for (Method method : type.getDeclaredMethods()) {
            if (method.getParameterCount() > 0) {
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

    private static Map<String, Method> getDeclaredSetters(Class<?> type) {
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

    private static Function<Object, Object> getGetter(Method declaredGetter) {
        return declaredGetter == null ? null : instance -> {
            try {
                return instance == null ? null : declaredGetter.invoke(instance);
            } catch (IllegalAccessException
                 | IllegalArgumentException
                 | InvocationTargetException exception) {
                throw new RuntimeException(exception);
            }
        };
    }

    private static BiConsumer<Object, Object> getSetter(Method declaredSetter) {
        return declaredSetter == null ? null : (instance, value) -> {
            try {
                declaredSetter.invoke(instance, value);
            } catch (IllegalAccessException
                 | IllegalArgumentException
                 | InvocationTargetException exception) {
                throw new RuntimeException(exception);
            }
        };
    }

    private static String camelize(String s) {
        char head = s.charAt(0);
        if (isUpperCase(head)) {
            return toLowerCase(head) + s.substring(1);
        } else {
            return s;
        }
    }

    public Set<String> getNames() {
        return declaredProperties.keySet();
    }

    public Property get(String name) {
        Property property = find(name);

        if (property == null) {
            String message = "No property found for '"
                + name + "' from " + sourceType + ".";
            throw new RuntimeException(message);
        }

        return property;
    }

    public Property find(String name) {
        Property declaredProperty = declaredProperties.getOrDefault(name, null);
        return declaredProperty != null
            ? declaredProperty
            : findFlattened(this, identity(), name, name);
    }

    private static Property findFlattened(
        Properties properties,
        Function<Object, Object> resolver,
        String path,
        String unresolvedPath
    ) {
        for (Property property : properties.declaredProperties.values()) {
            String propertyName = property.getName();
            if (unresolvedPath.equalsIgnoreCase(propertyName)) {
                return new Property(
                    property.getType(),
                    path,
                    instance -> property.getValue(resolver.apply(instance)),
                    null);
            } else if (path.toLowerCase().startsWith(propertyName.toLowerCase())) {
                return findFlattened(
                    Properties.get(property.getType()),
                    instance -> property.getValue(resolver.apply(instance)),
                    path,
                    path.substring(propertyName.length()));
            }
        }

        return null;
    }
}
