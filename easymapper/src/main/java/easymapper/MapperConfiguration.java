package easymapper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static easymapper.Exceptions.argumentNullException;
import static java.util.Collections.unmodifiableList;

public final class MapperConfiguration {

    private static final ConstructorExtractor defaultConstructorExtractor =
        t -> Arrays.asList(t.getConstructors());

    private static final ParameterNameResolver defaultParameterNameResolver =
        p -> p.isNamePresent() ? Optional.of(p.getName()) : Optional.empty();

    private ConstructorExtractor constructorExtractor;
    private ParameterNameResolver parameterNameResolver;
    private final List<MappingBuilder<?, ?>> mappings = new ArrayList<>();
    private final List<PropertyMappingBuilder<?, ?>> propertyMappings;
    private final List<PropertyMappingBuilder<?, ?>> unmodifiablePropertyMappings;

    MapperConfiguration() {
        constructorExtractor = defaultConstructorExtractor;
        parameterNameResolver = defaultParameterNameResolver;
        propertyMappings = new ArrayList<>();
        unmodifiablePropertyMappings = unmodifiableList(propertyMappings);
    }

    public ConstructorExtractor getConstructorExtractor() {
        return constructorExtractor;
    }

    public MapperConfiguration setConstructorExtractor(ConstructorExtractor value) {
        if (value == null) {
            throw argumentNullException("value");
        }

        this.constructorExtractor = value;

        return this;
    }

    public ParameterNameResolver getParameterNameResolver() {
        return parameterNameResolver;
    }

    public MapperConfiguration setParameterNameResolver(ParameterNameResolver value) {
        if (value == null) {
            throw argumentNullException("value");
        }

        this.parameterNameResolver = value;

        return this;
    }

    public <S, D> MapperConfiguration addProjector(
        Class<S> sourceType,
        Class<D> destinationType,
        BiFunction<S, D, Consumer<ProjectionContext>> consumer
    ) {
        if (sourceType == null) {
            throw argumentNullException("sourceType");
        }

        if (destinationType == null) {
            throw argumentNullException("destinationType");
        }

        if (consumer == null) {
            throw argumentNullException("consumer");
        }

        map(sourceType,
            destinationType,
            mapping -> mapping
                .project((source, destination) -> context -> consumer
                    .apply(source, destination)
                    .accept(ProjectionContext.fromMappingContext(context))));

        return this;
    }

    public <S, D> MapperConfiguration addProjector(
        TypeReference<S> sourceTypeReference,
        TypeReference<D> destinationTypeReference,
        BiFunction<S, D, Consumer<ProjectionContext>> consumer
    ) {
        if (sourceTypeReference == null) {
            throw argumentNullException("sourceTypeReference");
        }

        if (destinationTypeReference == null) {
            throw argumentNullException("destinationTypeReference");
        }

        if (consumer == null) {
            throw argumentNullException("consumer");
        }

        map(sourceTypeReference,
            destinationTypeReference,
            mapping -> mapping
                .project((source, destination) -> context -> consumer
                    .apply(source, destination)
                    .accept(ProjectionContext.fromMappingContext(context))));

        return this;
    }

    public MapperConfiguration addProjector(
        Function<Type, Boolean> sourceTypePredicate,
        Function<Type, Boolean> destinationTypePredicate,
        BiFunction<Object, Object, Consumer<ProjectionContext>> consumer
    ) {
        if (sourceTypePredicate == null) {
            throw argumentNullException("sourceTypePredicate");
        }

        if (destinationTypePredicate == null) {
            throw argumentNullException("destinationTypePredicate");
        }

        if (consumer == null) {
            throw argumentNullException("consumer");
        }

        map(sourceTypePredicate,
            destinationTypePredicate,
            mapping -> mapping
                .project((source, destination) -> context -> consumer
                    .apply(source, destination)
                    .accept(ProjectionContext.fromMappingContext(context))));

        return this;
    }

    public <S, D> MapperConfiguration addPropertyMapping(
        Class<S> sourceType,
        Class<D> destinationType,
        Consumer<PropertyMappingBuilder<S, D>> configurer
    ) {
        if (sourceType == null) {
            throw argumentNullException("sourceType");
        }

        if (destinationType == null) {
            throw argumentNullException("destinationType");
        }

        if (configurer == null) {
            throw argumentNullException("configurer");
        }

        PropertyMappingBuilder<S, D> builder = new PropertyMappingBuilder<>(sourceType, destinationType);
        configurer.accept(builder);

        propertyMappings.add(builder);

        return this;
    }

    public MapperConfiguration apply(Consumer<MapperConfiguration> configurer) {
        if (configurer == null) {
            throw argumentNullException("configurer");
        }

        configurer.accept(this);

        return this;
    }

    public MapperConfiguration map(
        Function<Type, Boolean> sourceTypePredicate,
        Function<Type, Boolean> destinationTypePredicate,
        Consumer<MappingBuilder<Object, Object>> configurer
    ) {
        if (sourceTypePredicate == null) {
            throw argumentNullException("sourceTypePredicate");
        } else if (destinationTypePredicate == null) {
            throw argumentNullException("destinationTypePredicate");
        } else if (configurer == null) {
            throw argumentNullException("configurer");
        }

        MappingBuilder<Object, Object> mapping = new MappingBuilder<>(
            sourceTypePredicate,
            destinationTypePredicate);

        configurer.accept(mapping);

        mappings.add(mapping);

        return this;
    }

    public <S, D> MapperConfiguration map(
        Class<S> sourceType,
        Class<D> destinationType,
        Consumer<MappingBuilder<S, D>> configurer
    ) {
        if (sourceType == null) {
            throw argumentNullException("sourceType");
        } else if (destinationType == null) {
            throw argumentNullException("destinationType");
        } else if (configurer == null) {
            throw argumentNullException("configurer");
        }

        MappingBuilder<S, D> mapping = new MappingBuilder<>(
            type -> type.equals(sourceType),
            type -> type.equals(destinationType));

        configurer.accept(mapping);

        mappings.add(mapping);

        return this;
    }

    public <S, D> MapperConfiguration map(
        TypeReference<S> sourceTypeReference,
        TypeReference<D> destinationTypeReference,
        Consumer<MappingBuilder<S, D>> configurer
    ) {
        if (sourceTypeReference == null) {
            throw argumentNullException("sourceTypeReference");
        } else if (destinationTypeReference == null) {
            throw argumentNullException("destinationTypeReference");
        } else if (configurer == null) {
            throw argumentNullException("configurer");
        }

        MappingBuilder<S, D> mapping = new MappingBuilder<>(
            type -> type.equals(sourceTypeReference.getType()),
            type -> type.equals(destinationTypeReference.getType()));

        configurer.accept(mapping);

        mappings.add(mapping);

        return this;
    }

    public Collection<MappingBuilder<?, ?>> getMappings() {
        return mappings;
    }

    public Collection<PropertyMappingBuilder<?, ?>> getPropertyMappings() {
        return unmodifiablePropertyMappings;
    }
}
