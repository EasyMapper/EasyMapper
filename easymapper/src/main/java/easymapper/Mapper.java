package easymapper;

import java.lang.reflect.Type;
import java.util.function.Consumer;

import lombok.NonNull;
import lombok.val;

public class Mapper {

    private final MappingConfiguration configuration;

    @SuppressWarnings("unused")
    public Mapper() {
        this(config -> { });
    }

    public Mapper(@NonNull Consumer<MapperConfigurationBuilder> configure) {
        MapperConfigurationBuilder builder = new MapperConfigurationBuilder()
            .apply(BaseConfiguration::configure)
            .apply(configure);

        configuration = builder.build();
    }

    public <S, T> T convert(
        S source,
        @NonNull Class<S> sourceType,
        @NonNull Class<T> targetType
    ) {
        return convertObject(source, sourceType, targetType);
    }

    public <S, T> T convert(
        @NonNull S source,
        @NonNull Class<T> targetType
    ) {
        return convertObject(source, source.getClass(), targetType);
    }

    public <S, T> T convert(
        S source,
        @NonNull TypeReference<S> sourceTypeReference,
        @NonNull TypeReference<T> targetTypeReference
    ) {
        Type sourceType = sourceTypeReference.getType();
        Type targetType = targetTypeReference.getType();
        return convertObject(source, sourceType, targetType);
    }

    @SuppressWarnings("unchecked")
    private <S, T> T convertObject(S source, Type sourceType, Type targetType) {
        val context = new MappingContext(configuration, sourceType, targetType);
        return (T) context.convert(source);
    }

    public <S, T> void project(
        @NonNull S source,
        @NonNull T target,
        @NonNull Class<S> sourceType,
        @NonNull Class<T> targetType
    ) {
        projectObject(source, target, sourceType, targetType);
    }

    public <S, T> void project(
        @NonNull S source,
        @NonNull T target,
        @NonNull TypeReference<S> sourceTypeReference,
        @NonNull TypeReference<T> targetTypeReference
    ) {
        Type sourceType = sourceTypeReference.getType();
        Type targetType = targetTypeReference.getType();
        projectObject(source, target, sourceType, targetType);
    }

    public void project(@NonNull Object source, @NonNull Object target) {
        projectObject(source, target, source.getClass(), target.getClass());
    }

    private void projectObject(
        Object source,
        Object target,
        Type sourceType,
        Type targetType
    ) {
        val context = new MappingContext(configuration, sourceType, targetType);
        context.project(source, target);
    }
}
