package easymapper;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import lombok.NonNull;

import static java.lang.System.lineSeparator;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;

public class Mapper {

    private final ConstructorExtractor constructorExtractor;
    private final ParameterNameResolver parameterNameResolver;
    private final List<Mapping<Object, Object>> mappings;

    public Mapper() {
        this(config -> {});
    }

    public Mapper(@NonNull Consumer<MapperConfiguration> configurer) {
        MapperConfiguration config = new MapperConfiguration()
            .apply(BaseConfiguration::configure)
            .apply(configurer);

        constructorExtractor = config.getConstructorExtractor();
        parameterNameResolver = config.getParameterNameResolver();
        mappings = getMappings(config);
    }

    @SuppressWarnings("unchecked")
    private static List<Mapping<Object, Object>> getMappings(
        MapperConfiguration config
    ) {
        return copyInReverseOrder(config
            .getMappings()
            .stream()
            .map(MappingBuilder::build)
            .map(mapping -> (Mapping<Object, Object>) mapping)
            .collect(toList()));
    }

    private static <T> List<T> copyInReverseOrder(Collection<T> list) {
        ArrayList<T> copy = new ArrayList<>(list);
        Collections.reverse(copy);
        return Collections.unmodifiableList(copy);
    }

    MappingContext createContext(Type sourceType, Type destinationType) {
        return new MappingContext(
            this,
            sourceType,
            destinationType,
            mappings
                .stream()
                .filter(m -> m.match(sourceType, destinationType))
                .findFirst()
                .orElse(Mapping.empty));
    }

    @SuppressWarnings("unchecked")
    public <S, D> D map(
        S source,
        @NonNull Class<S> sourceType,
        @NonNull Class<D> destinationType
    ) {
        return (D) createContext(sourceType, destinationType).convert(source);
    }

    @SuppressWarnings("unchecked")
    public <S, D> D map(
        S source,
        @NonNull TypeReference<S> sourceTypeReference,
        @NonNull TypeReference<D> destinationTypeReference
    ) {
        Type sourceType = sourceTypeReference.getType();
        Type destinationType = destinationTypeReference.getType();
        return (D) createContext(sourceType, destinationType).convert(source);
    }

    public <S, D> void map(
        @NonNull S source,
        @NonNull D destination,
        @NonNull Class<S> sourceType,
        @NonNull Class<D> destinationType
    ) {
        createContext(sourceType, destinationType).project(source, destination);
    }

    public <S, D> void map(
        @NonNull S source,
        @NonNull D destination,
        @NonNull TypeReference<S> sourceTypeReference,
        @NonNull TypeReference<D> destinationTypeReference
    ) {
        Type sourceType = sourceTypeReference.getType();
        Type destinationType = destinationTypeReference.getType();
        createContext(sourceType, destinationType).project(source, destination);
    }

    public void map(@NonNull Object source, @NonNull Object destination) {
        Type sourceType = source.getClass();
        Type destinationType = destination.getClass();
        createContext(sourceType, destinationType).project(source, destination);
    }

    Constructor<?> getConstructor(Type type) {
        if (type instanceof ParameterizedType) {
            return getConstructor(((ParameterizedType) type).getRawType());
        } else if (type instanceof Class<?>) {
            return getConstructor((Class<?>) type);
        } else {
            throw new RuntimeException(composeConstructorNotFoundMessage(type));
        }
    }

    private static String composeConstructorNotFoundMessage(Type type) {
        String newLine = lineSeparator();
        return "Cannot provide constructor for the type: " + type +
            newLine + "If you use Mapper to convert instances of generic classes, use the TypeReference<T> interface to specify the generic type." +
            newLine +
            newLine + "For example," +
            newLine +
            newLine + "mapper.map(" +
            newLine + "     source," +
            newLine + "     new TypeReference<DomainEvent<OrderPlaced>>() {}," +
            newLine + "     new TypeReference<IntegrationEvent<OrderPlacedEvent>>() {});";
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

    String[] getPropertyNames(Constructor<?> constructor) {
        return constructor.getParameterCount() == 0
            ? new String[0]
            : parameterNameResolver
                .tryResolveNames(constructor)
                .orElseGet(() -> getAnnotatedPropertyNames(constructor));
    }

    private static String[] getAnnotatedPropertyNames(
        Constructor<?> constructor
    ) {
        ConstructorProperties annotation =
            constructor.getAnnotation(ConstructorProperties.class);

        if (annotation == null) {
            String message = "The constructor " + constructor
                + " is not decorated with @ConstructorProperties annotation.";
            throw new RuntimeException(message);
        } else {
            return annotation.value();
        }
    }
}
