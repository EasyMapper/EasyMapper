package easymapper;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Optional;

import static easymapper.Exceptions.argumentNullException;
import static easymapper.Property.getProperties;
import static java.util.Comparator.comparingInt;

public final class Mapper {

    private static final String[] empty = new String[0];

    private final MapperConfiguration configuration;

    public Mapper() {
        this(MapperConfiguration.configureMapper(config -> { }));
    }

    public Mapper(MapperConfiguration configuration) {
        if (configuration == null) {
            throw argumentNullException("configuration");
        }

        this.configuration = configuration;
    }

    public <T> T map(Object source, Class<T> destinationType) {
        if (destinationType == null) {
            throw argumentNullException("destinationType");
        }

        if (source == null) {
            return null;
        }

        return map(source, source.getClass(), destinationType);
    }

    @SuppressWarnings("unchecked")
    private <T> T map(
        Object source,
        Class<?> sourceType,
        Class<T> destinationType
    ) {
        return configuration
            .findTransform(sourceType, destinationType)
            .map(x -> (T) x.transform(source))
            .orElseGet(() -> constructThenProject(source, sourceType, destinationType));
    }

    private <T> T constructThenProject(
        Object source,
        Class<?> sourceType,
        Class<T> destinationType
    ) {
        Object destination = construct(source, sourceType, destinationType);
        project(source, destination, sourceType, destinationType);
        return destinationType.cast(destination);
    }

    private Object construct(
        Object source,
        Class<?> sourceType,
        Class<?> destinationType
    ) {
        Constructor<?> constructor = getConstructor(destinationType);
        Parameter[] parameters = constructor.getParameters();
        String[] destinationPropertyNames = getPropertyNames(constructor);
        Map<String, Property> sourceProperties = getProperties(sourceType);
        Object[] arguments = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            String propertyName = destinationPropertyNames[i];
            Parameter parameter = parameters[i];

            arguments[i] = configuration
                .findMapping(sourceType, destinationType)
                .flatMap(mapping -> mapping.tryCalculate(source, propertyName))
                .orElseGet(() -> {
                    Property sourceProperty = sourceProperties.get(propertyName);
                    if (sourceProperty == null) {
                        String message = "No property found for '"
                            + propertyName + "' from " + sourceType + ".";
                        throw new RuntimeException(message);
                    }
                    return map(sourceProperty.getValue(source), parameter.getType());
                });
        }

        return createInstance(constructor, arguments);
    }

    private Constructor<?> getConstructor(Class<?> destinationType) {
        return configuration
            .getConstructorExtractor()
            .extract(destinationType)
            .stream()
            .max(comparingInt(Constructor::getParameterCount))
            .orElseThrow(() -> {
                String message = "No constructor found for " + destinationType;
                return new RuntimeException(message);
            });
    }

    private String[] getPropertyNames(Constructor<?> constructor) {
        if (constructor.getParameterCount() == 0) {
            return empty;
        }

        Parameter[] parameters = constructor.getParameters();

        return allParametersHaveNames(parameters)
            ? getParameterNames(parameters)
            : getAnnotatedPropertyNames(constructor);
    }

    private static boolean allParametersHaveNames(Parameter[] parameters) {
        for (Parameter parameter : parameters) {
            if (parameter.isNamePresent() == false) {
                return false;
            }
        }
        return true;
    }

    private static String[] getParameterNames(Parameter[] parameters) {
        String[] parameterNames = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            parameterNames[i] = parameters[i].getName();
        }
        return parameterNames;
    }

    private static String[] getAnnotatedPropertyNames(Constructor<?> constructor) {
        ConstructorProperties annotation = constructor.getAnnotation(ConstructorProperties.class);
        if (annotation == null) {
            String message = "The constructor " + constructor
                + " is not decorated with @ConstructorProperties annotation.";
            throw new RuntimeException(message);
        }
        return annotation.value();
    }

    private Object createInstance(
        Constructor<?> constructor,
        Object[] arguments
    ) {
        try {
            return constructor.newInstance(arguments);
        } catch (InstantiationException
             | IllegalAccessException
             | IllegalArgumentException
             | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void project(
        Object source,
        Object destination,
        Class<?> sourceType,
        Class<?> destinationType
    ) {
        Map<String, Property> sourceProperties = getProperties(sourceType);
        Map<String, Property> destinationProperties = getProperties(destinationType);

        for (String propertyName : destinationProperties.keySet()) {
            Optional<Object> calculated = configuration
                .findMapping(sourceType, destinationType)
                .flatMap(mapping -> mapping.tryCalculate(source, propertyName));

            if (calculated.isPresent()) {
                Property destinationProperty = destinationProperties.get(propertyName);
                destinationProperty.setValueIfPossible(
                    destination,
                    calculated.get());
                continue;
            }

            Property sourceProperty = sourceProperties.get(propertyName);

            if (sourceProperty == null) {
                continue;
            }

            Property destinationProperty = destinationProperties.get(propertyName);

            Object destinationPropertyValue = map(
                sourceProperty.getValue(source),
                destinationProperty.getType());

            destinationProperty.setValueIfPossible(destination, destinationPropertyValue);
        }
    }

    public void map(
        Object source,
        Object destination,
        Class<?> sourceType,
        Class<?> destinationType
    ) {
        if (source == null) {
            throw argumentNullException("source");
        }

        if (destination == null) {
            throw argumentNullException("destination");
        }

        project(source, destination, sourceType, destinationType);
    }
}
