package easymapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.function.Supplier;

public final class MappingContext {

    private final Mapper mapper;
    private final Type sourceType;
    private final Type destinationType;
    private final Mapping<Object, Object> mapping;

    MappingContext(
        Mapper mapper,
        Type sourceType,
        Type destinationType,
        Mapping<Object, Object> mapping
    ) {
        this.mapper = mapper;
        this.sourceType = sourceType;
        this.destinationType = destinationType;
        this.mapping = mapping;
    }

    Mapper getMapper() {
        return mapper;
    }

    Type getSourceType() {
        return sourceType;
    }

    Type getDestinationType() {
        return destinationType;
    }

    Properties getSourceProperties() {
        return Properties.get(sourceType);
    }

    Properties getDestinationProperties() {
        return Properties.get(destinationType);
    }

    Object convert(Object source) {
        return mapping
            .conversion()
            .map(conversion -> conversion.apply(this).apply(source))
            .orElseGet(() -> source == null ? null : constructThenProject(source));
    }

    private Object constructThenProject(Object source) {
        Object destination = construct(source);
        project(
            new Variable(sourceType, "source", source),
            new Variable(destinationType, "destination", destination));
        return destination;
    }

    private Object construct(Object source) {
        Constructor<?> constructor = mapper.getConstructor(destinationType);
        Parameter[] parameters = constructor.getParameters();
        String[] destinationPropertyNames = mapper.getPropertyNames(constructor);
        Object[] arguments = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            arguments[i] = compute(destinationPropertyNames[i], source);
        }

        try {
            return constructor.newInstance(arguments);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    void project(Variable source, Variable destination) {
        Object sourceValue = source.get();
        Object destinationValue = destination.get();

        if (sourceValue == destinationValue) {
            return;
        } else if (sourceValue == null) {
            String message = "'" + source.name() + "' is null but '" + destination.name() + "' is not null.";
            throw new RuntimeException(message);
        } else if (destinationValue == null) {
            String message = "'" + source.name() + "' is not null but '" + destination.name() + "' is null.";
            throw new RuntimeException(message);
        }

        mapping
            .projection()
            .<Runnable>map(projection -> () -> projection
                .apply(this)
                .accept(sourceValue, destinationValue))
            .orElse(() -> projectProperties(sourceValue, destinationValue))
            .run();
    }

    private void projectProperties(Object source, Object destination) {
        projectWritableProperties(source, destination);
        projectReadOnlyProperties(source, destination);
    }

    private void projectWritableProperties(Object source, Object destination) {
        Properties destinationProperties = getDestinationProperties();
        destinationProperties.useWritableProperties(destinationProperty ->
            computeIfPossible(destinationProperty.bind(destination), source));
    }

    private void projectReadOnlyProperties(Object source, Object destination) {
        Properties sourceProperties = getSourceProperties();
        Properties destinationProperties = getDestinationProperties();
        destinationProperties.useReadOnlyProperties(destinationProperty -> sourceProperties
            .ifPresent(destinationProperty.name(), sourceProperty -> mapper
                .createContext(sourceProperty.type(), destinationProperty.type())
                .project(
                    sourceProperty.bind(source),
                    destinationProperty.bind(destination))));
    }

    private Object compute(String destinationPropertyName, Object source) {
        return mapping
            .computation(destinationPropertyName)
            .<Supplier<Object>>map(computation -> () -> computation.apply(this).apply(source))
            .orElse(() -> {
                Property sourceProperty = getSourceProperties().get(destinationPropertyName);
                return mapper.map(
                    sourceProperty.get(source),
                    sourceProperty.type(),
                    getDestinationProperties().get(destinationPropertyName).type());
            })
            .get();
    }

    private void computeIfPossible(Variable destination, Object source) {
        mapping
            .computation(destination.name())
            .<Runnable>map(computation -> () ->
                destination.set(computation.apply(this).apply(source)))
            .orElse(() -> getDestinationProperties().ifPresent(
                destination.name(),
                destinationProperty -> {
                    Property sourceProperty = getSourceProperties().get(destination.name());
                    mapper
                        .createContext(sourceProperty.type(), destination.type())
                        .projectOrSet(sourceProperty.bind(source), destination);
                }))
            .run();
    }

    private void projectOrSet(Variable source, Variable destination) {
        Object sourceValue = source.get();

        if (sourceValue == destination.get()) {
            return;
        } else if (sourceValue == null) {
            String message = "The source '" + source.name() + "' is null.";
            throw new RuntimeException(message);
        }

        mapping
            .projection()
            .<Runnable>map(projection -> () -> projection
                .apply(this)
                .accept(
                    sourceValue,
                    destination.getOrSetIfNull(() -> convert(sourceValue))))
            .orElse(() -> destination.set(convert(sourceValue)))
            .run();
    }
}
