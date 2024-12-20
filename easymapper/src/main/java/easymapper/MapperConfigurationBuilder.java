package easymapper;

import java.util.function.Consumer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Getter(AccessLevel.PACKAGE)
@Accessors(fluent = true)
public final class MapperConfigurationBuilder {

    private ConstructorExtractor constructorExtractor;
    private ParameterNameResolver parameterNameResolver;
    private final ConverterContainerBuilder converters;
    private final ProjectorContainerBuilder projectors;
    private final ExtractorContainerBuilder extractors;

    MapperConfigurationBuilder() {
        constructorExtractor = DefaultConstructorExtractor.INSTANCE;
        parameterNameResolver = DefaultParameterNameResolver.INSTANCE;
        converters = new ConverterContainerBuilder();
        projectors = new ProjectorContainerBuilder();
        extractors = new ExtractorContainerBuilder();
    }

    public MapperConfigurationBuilder apply(
        @NonNull Consumer<MapperConfigurationBuilder> configurer
    ) {
        configurer.accept(this);
        return this;
    }

    public MapperConfigurationBuilder setConstructorExtractor(
        @NonNull ConstructorExtractor value
    ) {
        constructorExtractor = value;
        return this;
    }

    public MapperConfigurationBuilder setParameterNameResolver(
        @NonNull ParameterNameResolver value
    ) {
        parameterNameResolver = value;
        return this;
    }

    public <S, T> MapperConfigurationBuilder addConverter(
        @NonNull Class<S> sourceType,
        @NonNull Class<T> targetType,
        @NonNull Converter<S, T> converter
    ) {
        converters.add(
            TypePredicate.from(sourceType),
            TypePredicate.from(targetType),
            converter
        );
        return this;
    }

    public <S, T> MapperConfigurationBuilder addConverter(
        @NonNull TypePredicate sourceTypePredicate,
        @NonNull TypePredicate targetTypePredicate,
        @NonNull Converter<S, T> converter
    ) {
        converters.add(sourceTypePredicate, targetTypePredicate, converter);
        return this;
    }

    public <S, T> MapperConfigurationBuilder addProjector(
        @NonNull Class<S> sourceType,
        @NonNull Class<T> targetType,
        @NonNull Projector<S, T> projector
    ) {
        projectors.add(
            TypePredicate.from(sourceType),
            TypePredicate.from(targetType),
            projector
        );
        return this;
    }

    public <S, T> MapperConfigurationBuilder addProjector(
        @NonNull TypePredicate sourceTypePredicate,
        @NonNull TypePredicate targetTypePredicate,
        @NonNull Projector<S, T> projector
    ) {
        projectors.add(sourceTypePredicate, targetTypePredicate, projector);
        return this;
    }

    public <S, P> MapperConfigurationBuilder addExtractor(
        @NonNull Class<S> sourceType,
        @NonNull Class<?> targetType,
        @NonNull String targetPropertyName,
        @NonNull Extractor<S, P> extractor
    ) {
        extractors.add(
            TypePredicate.from(sourceType),
            TypePredicate.from(targetType),
            targetPropertyName,
            extractor
        );
        return this;
    }

    public <S, P> MapperConfigurationBuilder addExtractor(
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

    MappingConfiguration build() {
        return MappingConfiguration.build(this);
    }
}
