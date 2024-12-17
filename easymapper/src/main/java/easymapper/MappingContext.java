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

    private Object computeOrConvert(
        Object source,
        String destinationPropertyName
    ) {
        return mapping
            .computation(destinationPropertyName)
            .map(computation -> computation.bind(this, source))
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
            .<Runnable>map(projection -> () -> projection
                .project(this, source, destination))
            .orElse(() -> {
                setWritableProperties(source, destination);
                projectToReadOnlyProperties(source, destination);
            })
            .run();
    }

    private void setWritableProperties(Object source, Object destination) {
        Properties destinationProperties = getDestinationProperties();
        destinationProperties.useWritableProperties(destinationProperty ->
            computeOrConvertProperty(
                source,
                destinationProperty.bind(destination)));
    }

    private void computeOrConvertProperty(
        Object source,
        Variable destinationProperty
    ) {
        String name = destinationProperty.name();
        mapping
            .computation(name)
            .<Runnable>map(computation -> () ->
                destinationProperty.set(computation.compute(this, source)))
            .orElse(() -> getDestinationProperties().ifPresent(
                name,
                () -> convertIfPresent(source, destinationProperty)))
            .run();
    }

    private void convertIfPresent(Object source, Variable destination) {
        getSourceProperties().ifPresent(destination.name(), sourceProperty -> {
            MappingContext context = branchContext(
                sourceProperty.type(),
                destination.type());
            context.setProperty(sourceProperty.bind(source), destination);
        });
    }

    private void setProperty(Variable source, Variable destination) {
        Object sourceValue = source.get();

        if (sourceValue == destination.get()) {
            return;
        } else if (sourceValue == null) {
            String message = "The source '" + source.name() + "' is null.";
            throw new RuntimeException(message);
        }

        destination.set(convert(sourceValue));
    }

    private void projectToReadOnlyProperties(
        Object source,
        Object destination
    ) {
        getDestinationProperties().useReadOnlyProperties(destinationProperty ->
            projectIfPresent(source, destinationProperty.bind(destination)));
    }

    private void projectIfPresent(Object source, Variable destination) {
        getSourceProperties().ifPresent(destination.name(), sourceProperty -> {
            MappingContext context = branchContext(
                sourceProperty.type(),
                destination.type());
            context.project(sourceProperty.bind(source), destination);
        });
    }

    private void project(Variable source, Variable destination) {
        Object sourceValue = source.get();
        Object destinationValue = destination.get();

        if (sourceValue == null && destinationValue != null) {
            String message = "'" + source.name() + "' is null but '" + destination.name() + "' is not null.";
            throw new RuntimeException(message);
        } else if (sourceValue != null && destinationValue == null) {
            String message = "'" + source.name() + "' is not null but '" + destination.name() + "' is null.";
            throw new RuntimeException(message);
        }

        project(sourceValue, destinationValue);
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
