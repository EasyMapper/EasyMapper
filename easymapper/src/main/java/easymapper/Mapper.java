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

import static easymapper.Exceptions.argumentNullException;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;

public class Mapper {

    private final ConstructorExtractor constructorExtractor;
    private final ParameterNameResolver parameterNameResolver;
    private final List<Mapping<Object, Object>> mappings;

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
        mappings = getMappings(config);
    }

    @SuppressWarnings("unchecked")
    private static List<Mapping<Object, Object>> getMappings(
        MapperConfiguration config
    ) {
        return copyThenReverse(config
            .getMappings()
            .stream()
            .map(MappingBuilder::build)
            .map(mapping -> (Mapping<Object, Object>) mapping)
            .collect(toList()));
    }

    private static <T> List<T> copyThenReverse(Collection<T> list) {
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
    public <T> T map(Object source, Type sourceType, Type destinationType) {
        if (sourceType == null) {
            throw argumentNullException("sourceType");
        } else if (destinationType == null) {
            throw argumentNullException("destinationType");
        }

        return (T) createContext(sourceType, destinationType).convert(source);
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

        return (D) createContext(sourceType, destinationType).convert(source);
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

        return (D) createContext(sourceType, destinationType).convert(source);
    }

    public <S, D> void map(
        S source,
        D destination,
        Type sourceType,
        Type destinationType
    ) {
        if (source == null) {
            throw argumentNullException("source");
        } else if (destination == null) {
            throw argumentNullException("destination");
        } else if (sourceType == null) {
            throw argumentNullException("sourceType");
        } else if (destinationType == null) {
            throw argumentNullException("destinationType");
        }

        createContext(sourceType, destinationType).project(
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

        createContext(sourceType, destinationType).project(
            new Variable(sourceType, "source", source),
            new Variable(destinationType, "destination", destination));
    }

    public void map(Object source, Object destination) {
        if (source == null) {
            throw argumentNullException("source");
        } else if (destination == null) {
            throw argumentNullException("destination");
        }

        Type sourceType = source.getClass();
        Type destinationType = destination.getClass();

        createContext(sourceType, destinationType).project(
            new Variable(sourceType, "source", source),
            new Variable(destinationType, "destination", destination));
    }

    Constructor<?> getConstructor(Type type) {
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

    String[] getPropertyNames(Constructor<?> constructor) {
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
}
