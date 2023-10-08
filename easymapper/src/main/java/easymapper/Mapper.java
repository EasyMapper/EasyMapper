package easymapper;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static easymapper.Exceptions.argumentNullException;
import static easymapper.TypeAnalyzer.getParameterTypeResolver;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;

public class Mapper {

    private final ConstructorExtractor constructorExtractor;
    private final ParameterNameResolver parameterNameResolver;
    private final Collection<Converter> converters;
    private final Collection<Projector> projectors;
    private final Collection<Mapping> mappings;

    public Mapper() {
        this(config -> {});
    }

    public Mapper(Consumer<MapperConfiguration> configurer) {
        if (configurer == null) {
            throw argumentNullException("configurer");
        }

        MapperConfiguration config = new MapperConfiguration()
            .apply(BaseConfiguration::configurer)
            .apply(configurer);

        constructorExtractor = config.getConstructorExtractor();
        parameterNameResolver = config.getParameterNameResolver();

        converters = copyThenReverse(config.getConverters());
        projectors = copyThenReverse(config.getProjectors());

        mappings = copyThenReverse(config
            .getMappings()
            .stream()
            .map(MappingBuilder::build)
            .collect(toList()));
    }

    private static <T> Collection<T> copyThenReverse(Collection<T> list) {
        ArrayList<T> copy = new ArrayList<>(list);
        Collections.reverse(copy);
        return Collections.unmodifiableCollection(copy);
    }

    @SuppressWarnings("unchecked")
    public <T> T map(Object source, Type sourceType, Type destinationType) {
        if (sourceType == null) {
            throw argumentNullException("sourceType");
        } else if (destinationType == null) {
            throw argumentNullException("destinationType");
        }

        return (T) convert(source, sourceType, destinationType);
    }

    public <S, D> D map(S source, Class<S> sourceType, Class<D> destinationType) {
        return map(source, (Type) sourceType, destinationType);
    }

    public <S, D> D map(
        S source,
        TypeReference<S> sourceTypeReference,
        TypeReference<D> destinationTypeReference
    ) {
        if (sourceTypeReference == null) {
            throw argumentNullException("sourceTypeReference");
        } else if (destinationTypeReference == null) {
            throw argumentNullException("destinationTypeReference");
        }

        return map(
            source,
            sourceTypeReference.getType(),
            destinationTypeReference.getType());
    }

    public <S, D> void map(S source, D destination, Type sourceType, Type destinationType) {
        if (source == null) {
            throw argumentNullException("source");
        } else if (destination == null) {
            throw argumentNullException("destination");
        } else if (sourceType == null) {
            throw argumentNullException("sourceType");
        } else if (destinationType == null) {
            throw argumentNullException("destinationType");
        }

        projectToReadOnly(
            new VariableWrapper(sourceType, "source", () -> source),
            new VariableWrapper(destinationType, "destination", () -> destination));
    }

    public <S, D> void map(
        S source,
        D destination,
        TypeReference<S> sourceTypeReference,
        TypeReference<D> destinationTypeReference
    ) {
        if (sourceTypeReference == null) {
            throw argumentNullException("sourceTypeReference");
        } else if (destinationTypeReference == null) {
            throw argumentNullException("destinationTypeReference");
        }

        map(source,
            destination,
            sourceTypeReference.getType(),
            destinationTypeReference.getType());
    }

    private Object convert(Object source, Type sourceType, Type destinationType) {
        return findConverter(sourceType, destinationType)
            .map(x -> x.convert(
                source,
                new ConversionContext(this, sourceType, destinationType)))
            .orElseGet(() -> source == null
                ? null
                : constructThenProject(source, sourceType, destinationType));
    }

    private Optional<Converter> findConverter(Type sourceType, Type destinationType) {
        return converters
            .stream()
            .filter(converter -> converter.match(sourceType, destinationType))
            .findFirst();
    }

    private Object constructThenProject(Object source, Type sourceType, Type destinationType) {
        Object destination = construct(source, sourceType, destinationType);
        projectToReadOnly(
            new VariableWrapper(sourceType, "source", source),
            new VariableWrapper(destinationType, "destination", destination));
        return destination;
    }

    private Object construct(Object source, Type sourceType, Type destinationType) {
        Properties sourceProperties = Properties.get(sourceType);
        Function<Parameter, Type> parameterTypeResolver = getParameterTypeResolver(destinationType);
        Constructor<?> constructor = getConstructor(destinationType);
        Parameter[] parameters = constructor.getParameters();
        String[] destinationPropertyNames = getPropertyNames(constructor);
        Object[] arguments = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            String propertyName = destinationPropertyNames[i];
            Parameter parameter = parameters[i];

            arguments[i] = findMapping(sourceType, destinationType)
                .flatMap(mapping -> mapping.findCalculator(propertyName))
                .orElse(instance -> {
                    Property sourceProperty = sourceProperties.get(propertyName);
                    return convert(
                        sourceProperty.get(instance),
                        sourceProperty.type(),
                        parameterTypeResolver.apply(parameter));
                })
                .apply(source);
        }

        try {
            return constructor.newInstance(arguments);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private Optional<Mapping> findMapping(Type sourceType, Type destinationType) {
        return mappings
            .stream()
            .filter(mapping -> mapping.getSourceType().equals(sourceType))
            .filter(mapping -> mapping.getDestinationType().equals(destinationType))
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
            .orElseThrow(() -> new RuntimeException("No constructor found for " + type));
    }

    private String[] getPropertyNames(Constructor<?> constructor) {
        return constructor.getParameterCount() == 0
            ? new String[0]
            : parameterNameResolver
                .tryResolveNames(constructor)
                .orElseGet(() -> getAnnotatedPropertyNames(constructor));
    }

    private static String[] getAnnotatedPropertyNames(Constructor<?> constructor) {
        ConstructorProperties annotation = constructor.getAnnotation(ConstructorProperties.class);
        if (annotation == null) {
            String message = "The constructor " + constructor
                + " is not decorated with @ConstructorProperties annotation.";
            throw new RuntimeException(message);
        } else {
            return annotation.value();
        }
    }

    private void projectToReadOnly(VariableWrapper source, VariableWrapper destination) {
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

        findProjector(source.type(), destination.type())
            .orElseGet(this::getPropertyProjector)
            .project(
                sourceValue,
                destinationValue,
                new ProjectionContext(this, source.type(), destination.type()));
    }

    private Projector getPropertyProjector() {
        return Projector.create(
            type -> true,
            type -> true,
            (source, destination) -> context -> projectProperties(
                source,
                destination,
                context.getSourceType(),
                context.getDestinationType()));
    }

    private void projectProperties(
        Object source,
        Object destination,
        Type sourceType,
        Type destinationType
    ) {
        projectWritableProperties(source, destination, sourceType, destinationType);
        projectReadOnlyProperties(source, destination, sourceType, destinationType);
    }

    private void projectWritableProperties(
        Object source,
        Object destination,
        Type sourceType,
        Type destinationType
    ) {
        Properties sourceProperties = Properties.get(sourceType);
        Properties destinationProperties = Properties.get(destinationType);

        destinationProperties.useWritableProperties(destinationProperty ->
            findMapping(sourceType, destinationType)
                .flatMap(m -> m.findCalculator(destinationProperty.name()))
                .<Runnable>map(calculator -> () -> destinationProperty.set(
                    destination,
                    calculator.apply(source)))
                .orElse(() -> sourceProperties
                    .find(destinationProperty.name())
                    .ifPresent(sourceProperty -> projectToWritable(
                        sourceProperty.bind(source),
                        destinationProperty.bind(destination))))
                .run());
    }

    private void projectToWritable(VariableWrapper source, VariableWrapper destination) {
        findProjector(source.type(), destination.type())
            .<Runnable>map(projector -> () -> projectToWritable(source, destination, projector))
            .orElse(() -> destination.set(convert(source.get(), source.type(), destination.type())))
            .run();
    }

    private void projectToWritable(
        VariableWrapper source,
        VariableWrapper destination,
        Projector projector
    ) {
        Object sourceValue = source.get();

        if (sourceValue == destination.get()) {
            return;
        } else if (sourceValue == null) {
            throw new RuntimeException("The source '" + source.name() + "' is null.");
        }

        projector.project(
            sourceValue,
            destination.getOrSetIfNull(
                () -> convert(sourceValue, source.type(), destination.type())),
            new ProjectionContext(this, source.type(), destination.type()));
    }

    private void projectReadOnlyProperties(
        Object source,
        Object destination,
        Type sourceType,
        Type destinationType
    ) {
        Properties sourceProperties = Properties.get(sourceType);
        Properties destinationProperties = Properties.get(destinationType);

        destinationProperties.useReadOnlyProperties(destinationProperty -> sourceProperties
            .find(destinationProperty.name())
            .ifPresent(sourceProperty -> projectToReadOnly(
                sourceProperty.bind(source),
                destinationProperty.bind(destination))));
    }

    private Optional<Projector> findProjector(Type sourceType, Type destinationType) {
        return projectors
            .stream()
            .filter(projector -> projector.match(sourceType, destinationType))
            .findFirst();
    }
}
