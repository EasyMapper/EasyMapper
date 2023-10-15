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

    Type getSourceType() {
        return sourceType;
    }

    Type getDestinationType() {
        return destinationType;
    }

    MappingContext branchContext(Type sourceType, Type destinationType) {
        return mapper.createContext(sourceType, destinationType);
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
            arguments[i] = compute(source, destinationPropertyNames[i]);
        }

        try {
            return constructor.newInstance(arguments);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private Object compute(Object source, String destinationPropertyName) {
        return mapping
            .computation(destinationPropertyName)
            .<Supplier<Object>>map(computation -> () -> computation.apply(this).apply(source))
            .orElse(() -> convertProperty(source, destinationPropertyName))
            .get();
    }

    private Object convertProperty(Object source, String propertyName) {
        Property sourceProperty = getSourceProperty(propertyName);
        Property destinationProperty = getDestinationProperty(propertyName);
        MappingContext context = branchContext(
            sourceProperty.type(),
            destinationProperty.type());
        return context.convert(sourceProperty.get(source));
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
            computeIfPossible(source, destinationProperty.bind(destination)));
    }

    private void computeIfPossible(Object source, Variable destination) {
        String name = destination.name();
        mapping
            .computation(name)
            .<Runnable>map(computation -> () ->
                destination.set(computation.apply(this).apply(source)))
            .orElse(() -> getDestinationProperties().ifPresent(
                name,
                () -> projectOrSetProperty(source, destination)))
            .run();
    }

    private void projectOrSetProperty(Object source, Variable destination) {
        Property sourceProperty = getSourceProperty(destination.name());
        MappingContext context = branchContext(
            sourceProperty.type(),
            destination.type());
        context.projectOrSet(sourceProperty.bind(source), destination);
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

    private void projectReadOnlyProperties(Object source, Object destination) {
        getDestinationProperties().useReadOnlyProperties(destinationProperty ->
            projectPropertyIfPresent(source, destinationProperty.bind(destination)));
    }

    private void projectPropertyIfPresent(Object source, Variable destination) {
        getSourceProperties().ifPresent(destination.name(), sourceProperty -> {
            MappingContext context = branchContext(
                sourceProperty.type(),
                destination.type());
            context.project(sourceProperty.bind(source), destination);
        });
    }

    private Properties getSourceProperties() {
        return Properties.get(sourceType);
    }

    private Properties getDestinationProperties() {
        return Properties.get(destinationType);
    }

    private Property getDestinationProperty(String name) {
        return getDestinationProperties().get(name);
    }

    private Property getSourceProperty(String name) {
        return getSourceProperties().get(name);
    }
}
