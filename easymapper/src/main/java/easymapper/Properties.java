package easymapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

class Properties {

    private final Class<?> sourceType;
    private final Map<String, Property> statedProperties;

    private Properties(
        Class<?> sourceType,
        Map<String, Property> statedProperties
    ) {
        this.sourceType = sourceType;
        this.statedProperties = statedProperties;
    }

    public static Properties get(Class<?> sourceType) {
        return new Properties(sourceType, getStatedProperties(sourceType));
    }

    private static Map<String, Property> getStatedProperties(Class<?> type) {
        Map<String, Method> statedGetters = getStatedGetters(type);
        Map<String, Method> statedSetters = getStatedSetters(type);

        return statedGetters
            .keySet()
            .stream()
            .distinct()
            .map(name -> new Property(
                statedGetters.get(name).getReturnType(),
                name,
                getGetter(statedGetters.get(name)),
                getSetter(statedSetters.getOrDefault(name, null))))
            .collect(toMap(Property::getName, identity()));
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

    private static Map<String, Method> getStatedSetters(Class<?> type) {
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

    private static Function<Object, Object> getGetter(Method statedGetter) {
        return statedGetter == null ? null : instance -> {
            try {
                return instance == null ? null : statedGetter.invoke(instance);
            } catch (IllegalAccessException
                 | IllegalArgumentException
                 | InvocationTargetException exception) {
                throw new RuntimeException(exception);
            }
        };
    }

    private static BiConsumer<Object, Object> getSetter(Method statedSetter) {
        return statedSetter == null ? null : (instance, value) -> {
            try {
                statedSetter.invoke(instance, value);
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

    public Collection<Property> statedProperties() {
        return statedProperties.values();
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
        Property statedProperty = statedProperties.getOrDefault(name, null);
        return statedProperty != null
            ? statedProperty
            : findFlattened(this, identity(), name, name);
    }

    private static Property findFlattened(
        Properties properties,
        Function<Object, Object> resolver,
        String path,
        String unresolvedPath
    ) {
        for (Property property : properties.statedProperties.values()) {
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
