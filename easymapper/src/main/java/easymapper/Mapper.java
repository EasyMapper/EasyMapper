package easymapper;

import java.lang.reflect.Type;
import java.util.function.Consumer;

import lombok.NonNull;
import lombok.val;

public class Mapper {

    private final MappingSettings settings;

    @SuppressWarnings("unused")
    public Mapper() {
        this(config -> { });
    }

    public Mapper(@NonNull Consumer<MapperConfiguration> configurer) {
        MapperConfiguration config = new MapperConfiguration()
            .apply(BaseConfiguration::configure)
            .apply(configurer);

        settings = MappingSettings.from(config);
    }

    @Deprecated
    public <S, D> D map(
        S source,
        @NonNull Class<S> sourceType,
        @NonNull Class<D> destinationType
    ) {
        return convert(source, sourceType, destinationType);
    }

    public <S, D> D convert(
        S source,
        @NonNull Class<S> sourceType,
        @NonNull Class<D> destinationType
    ) {
        return convertObject(source, sourceType, destinationType);
    }

    public <S, D> D convert(S source, @NonNull Class<D> destinationType) {
        Type sourceType = source.getClass();
        return convertObject(source, sourceType, destinationType);
    }

    @Deprecated
    public <S, D> D map(
        S source,
        @NonNull TypeReference<S> sourceTypeReference,
        @NonNull TypeReference<D> destinationTypeReference
    ) {
        return convert(source, sourceTypeReference, destinationTypeReference);
    }

    public <S, D> D convert(
        S source,
        @NonNull TypeReference<S> sourceTypeReference,
        @NonNull TypeReference<D> destinationTypeReference
    ) {
        Type sourceType = sourceTypeReference.getType();
        Type destinationType = destinationTypeReference.getType();
        return convertObject(source, sourceType, destinationType);
    }

    @SuppressWarnings("unchecked")
    private <S, D> D convertObject(
        S source,
        Type sourceType,
        Type destinationType
    ) {
        val context = new MappingContext(settings, sourceType, destinationType);
        return (D) context.convert(source);
    }

    @Deprecated
    public <S, D> void map(
        @NonNull S source,
        @NonNull D destination,
        @NonNull Class<S> sourceType,
        @NonNull Class<D> destinationType
    ) {
        project(source, destination, sourceType, destinationType);
    }

    public <S, D> void project(
        @NonNull S source,
        @NonNull D destination,
        @NonNull Class<S> sourceType,
        @NonNull Class<D> destinationType
    ) {
        projectObject(source, destination, sourceType, destinationType);
    }

    @Deprecated
    public <S, D> void map(
        @NonNull S source,
        @NonNull D destination,
        @NonNull TypeReference<S> sourceTypeReference,
        @NonNull TypeReference<D> destinationTypeReference
    ) {
        project(
            source,
            destination,
            sourceTypeReference,
            destinationTypeReference
        );
    }

    public <S, D> void project(
        @NonNull S source,
        @NonNull D destination,
        @NonNull TypeReference<S> sourceTypeReference,
        @NonNull TypeReference<D> destinationTypeReference
    ) {
        Type sourceType = sourceTypeReference.getType();
        Type destinationType = destinationTypeReference.getType();
        projectObject(source, destination, sourceType, destinationType);
    }

    @Deprecated
    public void map(@NonNull Object source, @NonNull Object destination) {
        project(source, destination);
    }

    public void project(@NonNull Object source, @NonNull Object destination) {
        Type sourceType = source.getClass();
        Type destinationType = destination.getClass();
        projectObject(source, destination, sourceType, destinationType);
    }

    private void projectObject(
        Object source,
        Object destination,
        Type sourceType,
        Type destinationType
    ) {
        val context = new MappingContext(settings, sourceType, destinationType);
        context.project(source, destination);
    }
}
