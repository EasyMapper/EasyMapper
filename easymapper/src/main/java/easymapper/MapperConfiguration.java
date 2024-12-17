package easymapper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static easymapper.Exceptions.argumentNullException;

public final class MapperConfiguration {

    private ConstructorExtractor constructorExtractor;
    private ParameterNameResolver parameterNameResolver;
    private final List<MappingBuilder<?, ?>> mappings = new ArrayList<>();

    MapperConfiguration() {
        constructorExtractor = DefaultConstructorExtractor.INSTANCE;
        parameterNameResolver = DefaultParameterNameResolver.INSTANCE;
    }

    public ConstructorExtractor getConstructorExtractor() {
        return constructorExtractor;
    }

    public MapperConfiguration setConstructorExtractor(
        ConstructorExtractor value
    ) {
        if (value == null) {
            throw argumentNullException("value");
        }

        this.constructorExtractor = value;

        return this;
    }

    public ParameterNameResolver getParameterNameResolver() {
        return parameterNameResolver;
    }

    public MapperConfiguration setParameterNameResolver(
        ParameterNameResolver value
    ) {
        if (value == null) {
            throw argumentNullException("value");
        }

        this.parameterNameResolver = value;

        return this;
    }

    public Collection<MappingBuilder<?, ?>> getMappings() {
        return mappings;
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
}
