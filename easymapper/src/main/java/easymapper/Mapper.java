package easymapper;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static easymapper.Exceptions.argumentNullException;
import static easymapper.TypeAnalyzer.getParameterTypeResolver;
import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableList;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;

public class Mapper {

    private static final String[] empty = new String[0];

    private final ConstructorExtractor constructorExtractor;
    private final ParameterNameResolver parameterNameResolver;
    private final Collection<Transform> transforms;
    private final Collection<Mapping> mappings;

    public Mapper() {
        this(config -> { });
    }

    public Mapper(Consumer<MapperConfiguration> configurer) {
        if (configurer == null) {
            throw argumentNullException("configurer");
        }

        MapperConfiguration builder = new MapperConfiguration();

        configurer.accept(builder);

        constructorExtractor = builder.getConstructorExtractor();
        parameterNameResolver = builder.getParameterNameResolver();

        transforms = unmodifiableList(new ArrayList<>(builder.getTransforms()));

        mappings = unmodifiableList(builder
            .getMappings()
            .stream()
            .map(MappingBuilder::build)
            .collect(toList()));
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
        Type sourceType,
        Type destinationType
    ) {
        return findTransform(sourceType, destinationType)
            .map(x -> (T) x.transform(source))
            .orElseGet(() -> constructThenProject(source, sourceType, destinationType));
    }

    public <T> T map(Object source, TypeReference<T> destinationTypeReference) {
        if (destinationTypeReference == null) {
            throw argumentNullException("destinationTypeReference");
        }

        return map(source, destinationTypeReference.getType());
    }

    private <T> T map(Object source, Type destinationType) {
        if (source == null) {
            return null;
        }

        return map(source, source.getClass(), destinationType);
    }

    private Optional<Transform> findTransform(
        Type source,
        Type destination
    ) {
        return transforms.stream()
            .filter(transform -> transform.getSourceType().equals(source))
            .filter(transform -> transform.getDestinationType().equals(destination))
            .findFirst();
    }

    @SuppressWarnings("unchecked")
    private <T> T constructThenProject(
        Object source,
        Type sourceType,
        Type destinationType
    ) {
        Object destination = construct(source, sourceType, destinationType);
        project(source, destination, sourceType, destinationType);
        return (T) destination;
    }

    private Object construct(
        Object source,
        Type sourceType,
        Type destinationType
    ) {
        Function<Parameter, Type> parameterTypeResolver = getParameterTypeResolver(destinationType);

        Constructor<?> constructor = getConstructor(destinationType);
        Parameter[] parameters = constructor.getParameters();
        String[] destinationPropertyNames = getPropertyNames(constructor);
        Properties sourceProperties = Properties.get(sourceType);
        Object[] arguments = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            String propertyName = destinationPropertyNames[i];
            Parameter parameter = parameters[i];

            arguments[i] = findMapping(sourceType, destinationType)
                .flatMap(mapping -> mapping.findCalculator(propertyName))
                .orElseGet(() -> instance -> map(
                    sourceProperties.get(propertyName).getValue(instance),
                    parameterTypeResolver.apply(parameter))
                )
                .apply(source);
        }

        return createInstance(constructor, arguments);
    }

    private Optional<Mapping> findMapping(
        Type source,
        Type destination
    ) {
        return mappings.stream()
            .filter(mapping -> mapping.getSourceType().equals(source))
            .filter(mapping -> mapping.getDestinationType().equals(destination))
            .findFirst();
    }

    private Constructor<?> getConstructor(Type type) {
        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            if (rawType instanceof Class<?>) {
                return getConstructor((Class<?>) rawType);
            } else {
                String message = "Cannot provide constructor for the type: " + type;
                throw new RuntimeException(message);
            }
        } else if (type instanceof Class<?>) {
            return getConstructor((Class<?>) type);
        } else {
            String message = "Cannot provide constructor for the type: " + type;
            throw new RuntimeException(message);
        }
    }

    private Constructor<?> getConstructor(Class<?> type) {
        return constructorExtractor
            .extract(type)
            .stream()
            .max(comparingInt(Constructor::getParameterCount))
            .orElseThrow(() -> {
                String message = "No constructor found for " + type;
                return new RuntimeException(message);
            });
    }

    private String[] getPropertyNames(Constructor<?> constructor) {
        if (constructor.getParameterCount() == 0) {
            return empty;
        }

        List<String> names = stream(constructor.getParameters())
            .map(parameterNameResolver::tryResolveName)
            .map(x -> x.orElse(null))
            .collect(toList());

        return names.contains(null)
            ? getAnnotatedPropertyNames(constructor)
            : names.toArray(new String[0]);
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
        Type sourceType,
        Type destinationType
    ) {
        Properties sourceProperties = Properties.get(sourceType);
        Properties destinationProperties = Properties.get(destinationType);

        for (Property destinationProperty : destinationProperties.statedProperties()) {
            if (destinationProperty.isSettable() == false) {
                continue;
            }

            String propertyName = destinationProperty.getName();

            findMapping(sourceType, destinationType)
                .map(mapping -> mapping.findCalculator(propertyName))
                .orElseGet(() -> {
                    Property sourceProperty = sourceProperties.find(propertyName);
                    return Optional.ofNullable(sourceProperty == null
                        ? null
                        : instance -> map(
                            sourceProperty.getValue(instance),
                            destinationProperty.getType())
                    );
                })
                .ifPresent(calculator -> {
                    Object value = calculator.apply(source);
                    destinationProperty.setValue(destination, value);
                });
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

        if (sourceType == null) {
            throw argumentNullException("sourceType");
        }

        if (destinationType == null) {
            throw argumentNullException("destinationType");
        }

        project(source, destination, sourceType, destinationType);
    }

    public <S, D> void map(
        S source,
        D destination,
        TypeReference<S> sourceTypeReference,
        TypeReference<D> destinationTypeReference
    ) {
        if (source == null) {
            throw argumentNullException("source");
        }

        if (destination == null) {
            throw argumentNullException("destination");
        }

        if (sourceTypeReference == null) {
            throw argumentNullException("sourceTypeReference");
        }

        if (destinationTypeReference == null) {
            throw argumentNullException("destinationTypeReference");
        }

        project(
            source,
            destination,
            sourceTypeReference.getType(),
            destinationTypeReference.getType());
    }
}
