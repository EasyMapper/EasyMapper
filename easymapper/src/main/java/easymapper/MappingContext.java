package easymapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public final class MappingContext {

    private final Mapper mapper;

    @Getter(AccessLevel.PACKAGE)
    private final Type sourceType;

    @Getter(AccessLevel.PACKAGE)
    private final Type destinationType;

    private final Mapping<Object, Object> mapping;

    MappingContext branchContext(Type sourceType, Type destinationType) {
        return mapper.createContext(sourceType, destinationType);
    }

    Object convert(Object source) {
        return mapping
            .conversion()
            .map(conversion -> conversion.convert(this, source))
            .orElseGet(() -> convertInDefaultWay(source));
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
        Constructor<?> constructor = mapper.getConstructor(destinationType);
        Object[] arguments = buildArguments(source, constructor);
        return invoke(constructor, arguments);
    }

    private Object[] buildArguments(Object source, Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        String[] propertyNames = mapper.getPropertyNames(constructor);
        Object[] arguments = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            arguments[i] = computeOrConvert(source, propertyNames[i]);
        }

        return arguments;
    }

    private Object computeOrConvert(Object source, String propertyName) {
        return mapping
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

        MappingContext context = branchContext(
            sourceProperty.type(),
            destinationProperty.type());

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

        mapping
            .projection()
            .map(projection -> projection.bind(this, source, destination))
            .orElse(() -> projectInDefaultWay(source, destination))
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
        mapping
            .computation(property.name())
            .map(computation -> computation.bind(this, source))
            .<Runnable>map(factory -> () -> property.set(factory.get()))
            .orElse(() -> convertPropertyIfPresent(source, property))
            .run();
    }

    private void convertPropertyIfPresent(Object source, Variable destination) {
        Properties properties = Properties.get(sourceType);
        properties.ifPresent(destination.name(), property -> {
            MappingContext context = branchContext(
                property.type(),
                destination.type());
            context.convertThenSet(property.bind(source), destination);
        });
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
        properties.ifPresent(destination.name(), property -> {
            MappingContext context = branchContext(
                property.type(),
                destination.type());
            context.project(property.bind(source), destination);
        });
    }

    private void project(Variable source, Variable destination) {
        Object sourceValue = source.get();
        Object destinationValue = destination.get();

        if (sourceValue == null && destinationValue != null) {
            String message = "'"
                + source.name()
                + "' is null but '"
                + destination.name()
                + "' is not null.";
            throw new RuntimeException(message);
        } else if (sourceValue != null && destinationValue == null) {
            String message = "'"
                + source.name()
                + "' is not null but '"
                + destination.name()
                + "' is null.";
            throw new RuntimeException(message);
        }

        project(sourceValue, destinationValue);
    }
}
