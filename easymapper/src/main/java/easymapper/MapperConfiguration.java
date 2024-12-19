package easymapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.val;

public final class MapperConfiguration {

    @Getter
    @Accessors(fluent = true)
    private ConstructorExtractor constructorExtractor;

    @Getter
    @Accessors(fluent = true)
    private ParameterNameResolver parameterNameResolver;

    @Getter(AccessLevel.PACKAGE)
    private final List<MappingBuilder<?, ?>> mappings = new ArrayList<>();

    private final Converters converters = new Converters();

    private final Projectors projectors = new Projectors();

    private final Extractors extractors = new Extractors();

    MapperConfiguration() {
        constructorExtractor = DefaultConstructorExtractor.INSTANCE;
        parameterNameResolver = DefaultParameterNameResolver.INSTANCE;
    }

    Converters converters() {
        val converters = new Converters();
        converters.addRange(this.converters);
        return converters;
    }

    Projectors projectors() {
        val projectors = new Projectors();
        projectors.addRange(this.projectors);
        return projectors;
    }

    Extractors extractors() {
        val extractors = new Extractors();
        extractors.addRange(this.extractors);
        return extractors;
    }

    public MapperConfiguration apply(
        @NonNull Consumer<MapperConfiguration> configurer
    ) {
        configurer.accept(this);
        return this;
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

    public <S, T> MapperConfiguration addConverter(
        @NonNull Class<S> sourceType,
        @NonNull Class<T> targetType,
        @NonNull Converter<S, T> converter
    ) {
        converters.add(sourceType, targetType, converter);
        return this;
    }

    public <S, T> MapperConfiguration addConverter(
        @NonNull TypePredicate sourceTypePredicate,
        @NonNull TypePredicate targetTypePredicate,
        @NonNull Converter<S, T> converter
    ) {
        converters.add(sourceTypePredicate, targetTypePredicate, converter);
        return this;
    }

    public <S, T> MapperConfiguration addProjector(
        @NonNull Class<S> sourceType,
        @NonNull Class<T> targetType,
        @NonNull Projector<S, T> projector
    ) {
        projectors.add(sourceType, targetType, projector);
        return this;
    }

    public <S, T> MapperConfiguration addProjector(
        @NonNull TypePredicate sourceTypePredicate,
        @NonNull TypePredicate targetTypePredicate,
        @NonNull Projector<S, T> projector
    ) {
        projectors.add(sourceTypePredicate, targetTypePredicate, projector);
        return this;
    }

    public <S, P> MapperConfiguration addExtractor(
        @NonNull Class<S> sourceType,
        @NonNull Class<?> targetType,
        @NonNull String targetPropertyName,
        @NonNull Extractor<S, P> extractor
    ) {
        extractors.add(sourceType, targetType, targetPropertyName, extractor);
        return this;
    }

    public <S, P> MapperConfiguration addExtractor(
        @NonNull TypePredicate sourceTypePredicate,
        @NonNull TypePredicate targetTypePredicate,
        @NonNull String targetPropertyName,
        @NonNull Extractor<S, P> extractor
    ) {
        extractors.add(
            sourceTypePredicate,
            targetTypePredicate,
            targetPropertyName,
            extractor
        );
        return this;
    }

    public MapperConfiguration map(
        @NonNull TypePredicate sourceTypePredicate,
        @NonNull TypePredicate destinationTypePredicate,
        @NonNull Consumer<MappingBuilder<Object, Object>> configurer
    ) {
        MappingBuilder<Object, Object> mapping = new MappingBuilder<>(
            sourceTypePredicate,
            destinationTypePredicate
        );
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
            type -> type.equals(destinationType)
        );
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
            type -> type.equals(destinationTypeReference.getType())
        );
        configurer.accept(mapping);
        mappings.add(mapping);
        return this;
    }
}
