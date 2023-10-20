package easymapper;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static easymapper.TypeAnalyzer.getReturnTypeResolver;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
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
        Map<String, Setter> statedSetters = Setter.getStatedSetters(type);

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

    private static BiConsumer<Object, Object> getSetter(Setter statedSetter) {
        return statedSetter == null ? null : statedSetter::invoke;
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

    public Optional<Property> find(String name) {
        Property statedProperty = statedProperties.getOrDefault(name, null);
        if (statedProperty == null) {
            Property flattenedProperty = findFlattened(identity(), name, name);
            if (flattenedProperty == null) {
                return Optional.ofNullable(findUnflattened(name));
            } else {
                return Optional.of(flattenedProperty);
            }
        } else {
            return Optional.of(statedProperty);
        }
    }

    private Property findFlattened(
        Function<Object, Object> resolver,
        String path,
        String unresolvedPath
    ) {
        for (Property property : statedProperties.values()) {
            String propertyName = property.name();
            if (unresolvedPath.equalsIgnoreCase(propertyName)) {
                return new Property(
                    property.type(),
                    path,
                    instance -> property.get(resolver.apply(instance)),
                    null);
            } else if (path.toLowerCase().startsWith(propertyName.toLowerCase())) {
                return Properties.get(property.type()).findFlattened(
                    instance -> property.get(resolver.apply(instance)),
                    path,
                    path.substring(propertyName.length()));
            }
        }

        return null;
    }

    private Property findUnflattened(String name) {
        List<Property> innerProperties = statedProperties
            .values()
            .stream()
            .filter(property -> property.nameStartsWithIgnoreCase(name))
            .map(property -> property.withHeadTruncatedName(name.length()))
            .collect(toList());

        if (innerProperties.isEmpty()) {
            return null;
        }

        TupleType type = new TupleType(innerProperties
            .stream()
            .collect(toMap(Property::name, Property::type)));

        Function<Object, Object> getter = instance -> new Tuple(innerProperties
            .stream()
            .map(property -> property.bind(instance))
            .collect(toMap(Variable::name, Variable::get)));

        return new Property(type, name, getter, null);
    }
}
