package easymapper;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
    private final List<Mapping<Object, Object>> mappings;
    private final Collection<PropertyMapping> propertyMappings;

    public Mapper() {
        this(config -> {});
    }

    public Mapper(Consumer<MapperConfiguration> configurer) {
        if (configurer == null) {
            throw argumentNullException("configurer");
        }

        MapperConfiguration config = new MapperConfiguration()
            .apply(BaseConfiguration::configure)
            .apply(configurer);

        constructorExtractor = config.getConstructorExtractor();
        parameterNameResolver = config.getParameterNameResolver();
        mappings = copyThenReverse(getMappings(config));

        propertyMappings = copyThenReverse(config
            .getPropertyMappings()
            .stream()
            .map(PropertyMappingBuilder::build)
            .collect(toList()));
    }

    @SuppressWarnings("unchecked")
    private static List<Mapping<Object, Object>> getMappings(MapperConfiguration config) {
        return config
            .getMappings()
            .stream()
            .map(MappingBuilder::build)
            .map(mapping -> (Mapping<Object, Object>) mapping)
            .collect(toList());
    }

    private static <T> List<T> copyThenReverse(Collection<T> list) {
        ArrayList<T> copy = new ArrayList<>(list);
        Collections.reverse(copy);
        return Collections.unmodifiableList(copy);
    }

    @SuppressWarnings("unchecked")
    public <T> T map(Object source, Type sourceType, Type destinationType) {
        if (sourceType == null) {
            throw argumentNullException("sourceType");
        } else if (destinationType == null) {
            throw argumentNullException("destinationType");
        }

        return (T) convert(
            new Variable(sourceType, "source", source),
            destinationType);
    }

    @SuppressWarnings("unchecked")
    public <S, D> D map(
        S source,
        Class<S> sourceType,
        Class<D> destinationType
    ) {
        if (sourceType == null) {
            throw argumentNullException("sourceType");
        } else if (destinationType == null) {
            throw argumentNullException("destinationType");
        }

        return (D) convert(
            new Variable(sourceType, "source", source),
            destinationType);
    }

    @SuppressWarnings("unchecked")
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

        Type sourceType = sourceTypeReference.getType();
        Type destinationType = destinationTypeReference.getType();

        return (D) convert(
            new Variable(sourceType, "source", source),
            destinationType);
    }

    private Object convert(Variable source, Type destinationType) {
        Object sourceValue = source.get();
        return mappings.stream()
            .filter(m -> m.match(source.type(), destinationType))
            .findFirst()
            .flatMap(m -> m.hasConversion()
                ? Optional.ofNullable(
                    m.convert(
                        sourceValue,
                        new MappingContext(this, source.type(), destinationType)))
                : Optional.empty())
            .orElseGet(() -> sourceValue == null
                ? null
                : constructThenProject(sourceValue, source.type(), destinationType));
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

        project(
            new Variable(sourceType, "source", source),
            new Variable(destinationType, "destination", destination));
    }

    public <S, D> void map(
        S source,
        D destination,
        TypeReference<S> sourceTypeReference,
        TypeReference<D> destinationTypeReference
    ) {
        if (source == null) {
            throw argumentNullException("source");
        } else if (destination == null) {
            throw argumentNullException("destination");
        } else if (sourceTypeReference == null) {
            throw argumentNullException("sourceTypeReference");
        } else if (destinationTypeReference == null) {
            throw argumentNullException("destinationTypeReference");
        }

        Type sourceType = sourceTypeReference.getType();
        Type destinationType = destinationTypeReference.getType();

        project(
            new Variable(sourceType, "source", source),
            new Variable(destinationType, "destination", destination));
    }

    private void project(Variable source, Variable destination) {
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

        Optional<Mapping<Object, Object>> mapping = mappings
            .stream()
            .filter(m -> m.match(source.type(), destination.type()))
            .filter(Mapping::hasProjection)
            .findFirst();

        if (mapping.isPresent()) {
            mapping.get().project(
                sourceValue,
                destinationValue,
                new MappingContext(this, source.type(), destination.type()));
        } else {
            getPropertyProjector()
                .project(
                    sourceValue,
                    destinationValue,
                    new ProjectionContext(this, source.type(), destination.type()));
        }
    }

    private Object constructThenProject(Object source, Type sourceType, Type destinationType) {
        Object destination = construct(source, sourceType, destinationType);
        project(
            new Variable(sourceType, "source", source),
            new Variable(destinationType, "destination", destination));
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

            arguments[i] = mappings
                .stream()
                .filter(m -> m.matchSourceType(sourceType))
                .filter(m -> m.matchDestinationType(destinationType))
                .findFirst()
                .flatMap(m -> m.compute(propertyName, source, new MappingContext(this, sourceType, destinationType)))
                .orElseGet(() -> findMapping(sourceType, destinationType)
                    .flatMap(mapping -> mapping.findCalculator(propertyName))
                    .orElse(instance -> convert(
                        sourceProperties.get(propertyName).bind(instance),
                        parameterTypeResolver.apply(parameter)))
                    .apply(source));
        }

        try {
            return constructor.newInstance(arguments);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private Optional<PropertyMapping> findMapping(Type sourceType, Type destinationType) {
        return propertyMappings
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

        destinationProperties.useWritableProperties(destinationProperty -> {
            Optional<Object> computed = mappings.stream()
                .filter(m -> m.matchSourceType(sourceType))
                .filter(m -> m.matchDestinationType(destinationType))
                .findFirst()
                .flatMap(m -> m.compute(
                    destinationProperty.name(),
                    source,
                    new MappingContext(this, sourceType, destinationType)));

            if (computed.isPresent()) {
                destinationProperty.set(destination, computed.get());
            } else {
                findMapping(sourceType, destinationType)
                    .flatMap(m -> m.findCalculator(destinationProperty.name()))
                    .<Runnable>map(calculator -> () -> destinationProperty.set(
                        destination,
                        calculator.apply(source)))
                    .orElse(() -> sourceProperties
                        .find(destinationProperty.name())
                        .ifPresent(sourceProperty -> projectOrSet(
                            sourceProperty.bind(source),
                            destinationProperty.bind(destination))))
                    .run();
            }
        });
    }

    private void projectOrSet(Variable source, Variable destination) {
        Optional<Mapping<Object, Object>> mapping = mappings
            .stream()
            .filter(m -> m.match(source.type(), destination.type()))
            .filter(Mapping::hasProjection)
            .findFirst();

        if (mapping.isPresent()) {
            projectOrSet(
                source,
                destination,
                Projector.create(
                    type -> mapping.get().matchSourceType(type),
                    type -> mapping.get().matchDestinationType(type),
                    (s, d) -> context -> mapping.get().project(
                        s,
                        d,
                        context.toMappingContext())));
        } else {
            destination.set(convert(source, destination.type()));
        }
    }

    private void projectOrSet(
        Variable source,
        Variable destination,
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
            destination.getOrSetIfNull(() -> convert(source, destination.type())),
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
            .ifPresent(sourceProperty -> project(
                sourceProperty.bind(source),
                destinationProperty.bind(destination))));
    }
}
