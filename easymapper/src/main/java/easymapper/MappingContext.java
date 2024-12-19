package easymapper;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Supplier;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static java.lang.System.lineSeparator;
import static java.util.Comparator.comparingInt;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public final class MappingContext {

    private final MappingSettings settings;

    @Getter(AccessLevel.PACKAGE)
    private final Type sourceType;

    @Getter(AccessLevel.PACKAGE)
    private final Type destinationType;

    MappingContext branch(Type sourceType, Type destinationType) {
        return new MappingContext(settings, sourceType, destinationType);
    }

    Object convert(Object source) {
        return settings
            .mappings()
            .stream()
            .filter(mapping -> mapping.match(sourceType, destinationType))
            .findFirst()
            .orElse(Mapping.EMPTY)
            .conversion()
            .map(conversion -> conversion.bind(this, source))
            .orElseGet(() -> settings
                .converters()
                .find(sourceType, destinationType)
                .<Supplier<Object>>map(converter -> () -> converter.convert(this, source))
                .orElse(() -> convertInDefaultWay(source)))
            .get();
    }

    private Object convertInDefaultWay(Object source) {
        return source == null ? null : constructThenProject(source);
    }

    private Object constructThenProject(Object source) {
        Object destination = construct(source);
        project(source, destination);
        return destination;
    }

    private Object construct(Object source) {
        Constructor<?> constructor = getConstructor(destinationType);
        Object[] arguments = buildArguments(source, constructor);
        return invoke(constructor, arguments);
    }

    private Constructor<?> getConstructor(Type type) {
        if (type instanceof ParameterizedType) {
            return getConstructor(((ParameterizedType) type).getRawType());
        } else if (type instanceof Class<?>) {
            return getConstructor((Class<?>) type);
        } else {
            throw new RuntimeException(composeConstructorNotFoundMessage(type));
        }
    }

    private static String composeConstructorNotFoundMessage(Type type) {
        String newLine = lineSeparator();
        return "Cannot provide constructor for the type: " + type
            + newLine + "If you use Mapper to convert instances of generic classes, use the TypeReference<T> interface to specify the generic type."
            + newLine
            + newLine + "For example,"
            + newLine
            + newLine + "mapper.convert("
            + newLine + "     source,"
            + newLine + "     new TypeReference<DomainEvent<OrderPlaced>>() {},"
            + newLine + "     new TypeReference<IntegrationEvent<OrderPlacedEvent>>() {});";
    }

    private Constructor<?> getConstructor(Class<?> type) {
        return settings
            .constructorExtractor()
            .extract(type)
            .stream()
            .max(comparingInt(Constructor::getParameterCount))
            .orElseThrow(() -> {
                String message = "No constructor found for " + type;
                return new RuntimeException(message);
            });
    }

    private Object[] buildArguments(Object source, Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        String[] propertyNames = getPropertyNames(constructor);
        Object[] arguments = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            arguments[i] = computeOrConvert(source, propertyNames[i]);
        }

        return arguments;
    }

    private String[] getPropertyNames(Constructor<?> constructor) {
        return settings
            .parameterNameResolver()
            .tryResolveNames(constructor)
            .orElseGet(() -> getAnnotatedPropertyNames(constructor));
    }

    private static String[] getAnnotatedPropertyNames(
        Constructor<?> constructor
    ) {
        ConstructorProperties annotation = constructor
            .getAnnotation(ConstructorProperties.class);

        if (annotation == null) {
            String message = "The constructor " + constructor
                + " is not decorated with @ConstructorProperties annotation.";
            throw new RuntimeException(message);
        } else {
            return annotation.value();
        }
    }

    private Object computeOrConvert(Object source, String propertyName) {
        return settings
            .mappings()
            .stream()
            .filter(mapping -> mapping.match(sourceType, destinationType))
            .findFirst()
            .orElse(Mapping.EMPTY)
            .computation(propertyName)
            .map(computation -> computation.bind(this, source))
            .orElse(() -> convertProperty(source, propertyName))
            .get();
    }

    private Object convertProperty(Object source, String propertyName) {
        Property sourceProperty = Properties
            .get(sourceType)
            .get(propertyName);

        Property destinationProperty = Properties
            .get(destinationType)
            .get(propertyName);

        MappingContext context = branch(
            sourceProperty.type(),
            destinationProperty.type()
        );

        return context.convert(sourceProperty.get(source));
    }

    private static Object invoke(
        Constructor<?> constructor,
        Object[] arguments
    ) {
        try {
            return constructor.newInstance(arguments);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    void project(Object source, Object destination) {
        if (source == destination) {
            return;
        }

        settings
            .mappings()
            .stream()
            .filter(mapping -> mapping.match(sourceType, destinationType))
            .findFirst()
            .orElse(Mapping.EMPTY)
            .projection()
            .map(projection -> projection.bind(this, source, destination))
            .orElseGet(() -> settings
                .projectors()
                .find(sourceType, destinationType)
                .<Runnable>map(projector -> () -> projector.project(this, source, destination))
                .orElse(() -> projectInDefaultWay(source, destination)))
            .run();
    }

    private void projectInDefaultWay(Object source, Object destination) {
        setWritableProperties(source, destination);
        projectToReadOnlyProperties(source, destination);
    }

    private void setWritableProperties(Object source, Object destination) {
        Properties properties = Properties.get(destinationType);
        properties.useWritableProperties(property ->
            computeOrConvertProperty(source, property.bind(destination)));
    }

    private void computeOrConvertProperty(Object source, Variable property) {
        settings
            .mappings()
            .stream()
            .filter(mapping -> mapping.match(sourceType, destinationType))
            .findFirst()
            .orElse(Mapping.EMPTY)
            .computation(property.name())
            .map(computation -> computation.bind(this, source))
            .<Runnable>map(factory -> () -> property.set(factory.get()))
            .orElse(() -> convertPropertyIfPresent(source, property))
            .run();
    }

    private void convertPropertyIfPresent(Object source, Variable destination) {
        Properties properties = Properties.get(sourceType);
        properties.ifPresent(
            destination.name(), property -> {
                MappingContext context = branch(
                    property.type(),
                    destination.type()
                );
                context.convertThenSet(property.bind(source), destination);
            }
        );
    }

    private void convertThenSet(Variable source, Variable destination) {
        Object sourceValue = source.get();

        if (sourceValue == destination.get()) {
            return;
        }

        if (sourceValue == null) {
            String message = "The source '" + source.name() + "' is null.";
            throw new RuntimeException(message);
        } else {
            Object destinationValue = convert(sourceValue);
            destination.set(destinationValue);
        }
    }

    private void projectToReadOnlyProperties(
        Object source,
        Object destination
    ) {
        Properties properties = Properties.get(destinationType);
        properties.useReadOnlyProperties(property ->
            projectPropertyIfPresent(source, property.bind(destination)));
    }

    private void projectPropertyIfPresent(Object source, Variable destination) {
        Properties properties = Properties.get(sourceType);
        properties.ifPresent(
            destination.name(),
            property -> {
                MappingContext context = branch(
                    property.type(),
                    destination.type()
                );
                context.project(property.bind(source).get(), destination.get());
            }
        );
    }
}
