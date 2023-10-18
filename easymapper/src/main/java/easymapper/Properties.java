package easymapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static easymapper.TypeAnalyzer.getReturnTypeResolver;
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
        Map<String, Getter> statedGetters = Getter.getStatedGetters(type);
        Map<String, Method> statedSetters = Setter.getStatedSetters(type);

        Function<Getter, Type> returnTypeResolver = getReturnTypeResolver(type);

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

    private static Function<Object, Object> getGetter(Getter statedGetter) {
        return statedGetter == null
            ? null
            : instance -> instance == null
                ? null
                : statedGetter.invoke(instance);
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

    public void useWritableProperties(Consumer<Property> consumer) {
        for (Property property : statedProperties.values()) {
            if (property.isWritable()) {
                consumer.accept(property);
            }
        }
    }

    public void useReadOnlyProperties(Consumer<Property> consumer) {
        for (Property property : statedProperties.values()) {
            if (property.isReadOnly()) {
                consumer.accept(property);
            }
        }
    }

    public Property get(String name) {
        return find(name).orElseThrow(() -> {
            String message = "No property found for '"
                + name + "' from " + sourceType + ".";
            return new RuntimeException(message);
        });
    }

    public void ifPresent(String name, Consumer<Property> action) {
        find(name).ifPresent(action);
    }

    public void ifPresent(String name, Runnable action) {
        ifPresent(name, property -> action.run());
    }

    private Optional<Property> find(String name) {
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
