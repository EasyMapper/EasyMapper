package easymapper;

import java.util.function.Consumer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Getter(AccessLevel.PACKAGE)
@Accessors(fluent = true)
public final class MapperConfiguration {

    private ConstructorExtractor constructorExtractor;
    private ParameterNameResolver parameterNameResolver;
    private final ConverterContainerBuilder converters;
    private final ProjectorContainerBuilder projectors;
    private final ExtractorContainerBuilder extractors;

    MapperConfiguration() {
        constructorExtractor = DefaultConstructorExtractor.INSTANCE;
        parameterNameResolver = DefaultParameterNameResolver.INSTANCE;
        converters = new ConverterContainerBuilder();
        projectors = new ProjectorContainerBuilder();
        extractors = new ExtractorContainerBuilder();
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
        constructorExtractor = value;
        return this;
    }

    public MapperConfiguration setParameterNameResolver(
        @NonNull ParameterNameResolver value
    ) {
        parameterNameResolver = value;
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
}
