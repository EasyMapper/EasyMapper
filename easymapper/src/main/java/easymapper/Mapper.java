package easymapper;

import static easymapper.ConstructorSelector.getConstructor;
import static easymapper.Exceptions.argumentNullException;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.UUID;

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

        Class<?> sourceType = source.getClass();
        Object destination = construct(source, sourceType, destinationType);
        project(source, destination, sourceType, destinationType);
        return destinationType.cast(destination);
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

    private Object construct(
        Object source,
        Class<?> sourceType,
        Class<?> destinationType
    ) {
        Constructor<?> constructor = getConstructor(destinationType);
        Parameter[] parameters = constructor.getParameters();
        Object[] arguments = new Object[parameters.length];
        Map<String, Property> sourceProperties = Property.getProperties(sourceType);
        String[] destinationPropertyNames = getPropertyNames(constructor);
        for (int i = 0; i < parameters.length; i++) {
            String destinationPropertyName = destinationPropertyNames[i];
            String sourcePropertyName = getSourcePropertyName(
                sourceType,
                destinationType,
                destinationPropertyName);
            Property sourceProperty = sourceProperties.get(sourcePropertyName);
            Object sourcePropertyValue = sourceProperty.getValue(source);
            Parameter parameter = parameters[i];
            if (parameter.getType().isPrimitive()
                || parameter.getType().equals(String.class)
                || parameter.getType().equals(UUID.class)) {
                arguments[i] = sourcePropertyValue;
            } else {
                arguments[i] = map(sourcePropertyValue, parameter.getType());
            }
        }

        return createInstance(constructor, arguments);
    }

    private void project(
        Object source,
        Object destination,
        Class<?> sourceType,
        Class<?> destinationType
    ) {
        Map<String, Property> sourceProperties = Property.getProperties(sourceType);
        Map<String, Property> destinationProperties = Property.getProperties(destinationType);
        for (String destinationPropertyName : destinationProperties.keySet()) {
            String sourcePropertyName = getSourcePropertyName(
                sourceType,
                destinationType,
                destinationPropertyName);
            if (sourceProperties.containsKey(sourcePropertyName)) {
                Object sourcePropertyValue = sourceProperties
                    .get(sourcePropertyName)
                    .getValue(source);
                destinationProperties
                    .get(destinationPropertyName)
                    .setValueIfPossible(destination, sourcePropertyValue);
            }
        }
    }

    private String getSourcePropertyName(
        Class<?> sourceType,
        Class<?> destinationType,
        String destinationPropertyName
    ) {
        return configuration
            .getMapping(sourceType, destinationType)
            .flatMap(mapping -> mapping.getSourcePropertyName(destinationPropertyName))
            .orElse(destinationPropertyName);
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
}
