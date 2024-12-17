package easymapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

public final class MapperConfiguration {

    @Getter
    private ConstructorExtractor constructorExtractor;

    @Getter
    private ParameterNameResolver parameterNameResolver;

    @Getter(AccessLevel.PACKAGE)
    private final List<MappingBuilder<?, ?>> mappings = new ArrayList<>();

    MapperConfiguration() {
        constructorExtractor = DefaultConstructorExtractor.INSTANCE;
        parameterNameResolver = DefaultParameterNameResolver.INSTANCE;
    }

    public MapperConfiguration setConstructorExtractor(
        @NonNull ConstructorExtractor value
    ) {
        this.constructorExtractor = value;
        return this;
    }

    public MapperConfiguration setParameterNameResolver(
        @NonNull ParameterNameResolver value
    ) {
        this.parameterNameResolver = value;
        return this;
    }

    public MapperConfiguration apply(
        @NonNull Consumer<MapperConfiguration> configurer
    ) {
        configurer.accept(this);
        return this;
    }

    public MapperConfiguration map(
        @NonNull TypePredicate sourceTypePredicate,
        @NonNull TypePredicate destinationTypePredicate,
        @NonNull Consumer<MappingBuilder<Object, Object>> configurer
    ) {
        MappingBuilder<Object, Object> mapping = new MappingBuilder<>(
            sourceTypePredicate,
            destinationTypePredicate);
        configurer.accept(mapping);
        mappings.add(mapping);
        return this;
    }

    public <S, D> MapperConfiguration map(
        @NonNull Class<S> sourceType,
        @NonNull Class<D> destinationType,
        @NonNull Consumer<MappingBuilder<S, D>> configurer
    ) {
        MappingBuilder<S, D> mapping = new MappingBuilder<>(
            type -> type.equals(sourceType),
            type -> type.equals(destinationType));
        configurer.accept(mapping);
        mappings.add(mapping);
        return this;
    }

    public <S, D> MapperConfiguration map(
        @NonNull TypeReference<S> sourceTypeReference,
        @NonNull TypeReference<D> destinationTypeReference,
        @NonNull Consumer<MappingBuilder<S, D>> configurer
    ) {
        MappingBuilder<S, D> mapping = new MappingBuilder<>(
            type -> type.equals(sourceTypeReference.getType()),
            type -> type.equals(destinationTypeReference.getType()));
        configurer.accept(mapping);
        mappings.add(mapping);
        return this;
    }
}
