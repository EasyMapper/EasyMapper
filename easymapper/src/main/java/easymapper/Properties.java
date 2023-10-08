package easymapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static easymapper.TypeAnalyzer.getReturnTypeResolver;
import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

class Properties {

    private final Type sourceType;
    private final Map<String, Property> statedProperties;

    private Properties(
        Type sourceType,
        Map<String, Property> statedProperties
    ) {
        this.sourceType = sourceType;
        this.statedProperties = statedProperties;
    }

    public static Properties get(Type sourceType) {
        return new Properties(sourceType, getStatedProperties(sourceType));
    }

    private static Map<String, Property> getStatedProperties(Type type) {
        Map<String, Method> statedGetters = getStatedGetters(type);
        Map<String, Method> statedSetters = getStatedSetters(type);

        Function<Method, Type> returnTypeResolver = getReturnTypeResolver(type);

        return statedGetters
            .keySet()
            .stream()
            .distinct()
            .map(name -> new Property(
                returnTypeResolver.apply(statedGetters.get(name)),
                name,
                getGetter(statedGetters.get(name)),
                getSetter(statedSetters.getOrDefault(name, null))))
            .collect(toMap(Property::name, identity()));
    }

    private static Map<String, Method> getStatedGetters(Type type) {
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

    private static Map<String, Method> getStatedSetters(Type type) {
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

    private static String camelize(String s) {
        char head = s.charAt(0);
        if (isUpperCase(head)) {
            return toLowerCase(head) + s.substring(1);
        } else {
            return s;
        }
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

    public Collection<Property> statedProperties() {
        return statedProperties.values();
    }

    public Property get(String name) {
        return find(name).orElseThrow(() -> {
            String message = "No property found for '"
                + name + "' from " + sourceType + ".";
            return new RuntimeException(message);
        });
    }

    public Optional<Property> find(String name) {
        Property statedProperty = statedProperties.getOrDefault(name, null);
        return statedProperty != null
            ? Optional.of(statedProperty)
            : findFlattened(this, identity(), name, name);
    }

    private static Optional<Property> findFlattened(
        Properties properties,
        Function<Object, Object> resolver,
        String path,
        String unresolvedPath
    ) {
        for (Property property : properties.statedProperties.values()) {
            String propertyName = property.name();
            if (unresolvedPath.equalsIgnoreCase(propertyName)) {
                return Optional.of(
                    new Property(
                        property.type(),
                        path,
                        instance -> property.get(resolver.apply(instance)),
                        null));
            } else if (path.toLowerCase().startsWith(propertyName.toLowerCase())) {
                return findFlattened(
                    Properties.get(property.type()),
                    instance -> property.get(resolver.apply(instance)),
                    path,
                    path.substring(propertyName.length()));
            }
        }

        return Optional.empty();
    }
}
