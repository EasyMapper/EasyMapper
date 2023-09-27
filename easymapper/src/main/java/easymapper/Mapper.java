package easymapper;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.UUID;

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
        String[] destinationPropertyNames = getPropertyNames(constructor);
        Map<String, Property> sourceProperties = getProperties(sourceType);

        Object[] arguments = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            String sourcePropertyName = getSourcePropertyName(
                sourceType,
                destinationType,
                destinationPropertyNames[i]);
            Object sourcePropertyValue = sourceProperties.get(sourcePropertyName).getValue(source);
            arguments[i] = transform(sourcePropertyValue, parameters[i].getType());
        }

        return createInstance(constructor, arguments);
    }

    private Object transform(
        Object sourceValue,
        Class<?> destinationType
    ) {
        return destinationType.isPrimitive()
            || destinationType.equals(String.class)
            || destinationType.equals(UUID.class)
            ? sourceValue
            : map(sourceValue, destinationType);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private Constructor<?> getConstructor(Class<?> destinationType) {
        return configuration
            .getConstructorExtractor()
            .extract(destinationType)
            .stream()
            .max(comparingInt(Constructor::getParameterCount))
            .get();
    }

    private void project(
        Object source,
        Object destination,
        Class<?> sourceType,
        Class<?> destinationType
    ) {
        Map<String, Property> sourceProperties = getProperties(sourceType);
        Map<String, Property> destinationProperties = getProperties(destinationType);

        for (String destinationPropertyName : destinationProperties.keySet()) {
            String sourcePropertyName = getSourcePropertyName(
                sourceType,
                destinationType,
                destinationPropertyName);

            Property sourceProperty = sourceProperties.getOrDefault(sourcePropertyName, null);

            if (sourceProperty == null) {
                continue;
            }

            Property destinationProperty = destinationProperties.get(destinationPropertyName);
            Class<?> destinationPropertyType = destinationProperty.getType();
            Object destinationPropertyValue = transform(
                sourceProperty.getValue(source),
                destinationPropertyType);

            destinationProperty.setValueIfPossible(destination, destinationPropertyValue);
        }
    }

    private String getSourcePropertyName(
        Class<?> sourceType,
        Class<?> destinationType,
        String destinationPropertyName
    ) {
        return configuration
            .findMapping(sourceType, destinationType)
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
