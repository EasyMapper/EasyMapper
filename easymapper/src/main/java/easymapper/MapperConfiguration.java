package easymapper;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

public final class MapperConfiguration {

    private final ConstructorExtractor constructorExtractor;
    private final Collection<Transform> transforms;
    private final Collection<Mapping> mappings;

    private MapperConfiguration(
        ConstructorExtractor constructorExtractor,
        Collection<Transform> transforms,
        Collection<Mapping> mappings
    ) {
        this.constructorExtractor = constructorExtractor;
        this.transforms = transforms;
        this.mappings = mappings;
    }

    public static MapperConfiguration configureMapper(
        Consumer<MapperConfigurationBuilder> configurer
    ) {
        if (configurer == null) {
            throw Exceptions.argumentNullException("configurer");
        }

        MapperConfigurationBuilder builder = new MapperConfigurationBuilder();
        configurer.accept(builder);
        return new MapperConfiguration(
            builder.getConstructorExtractor(),
            builder.getTransforms(),
            builder.getMappings()
        );
    }

    public ConstructorExtractor getConstructorExtractor() {
        return constructorExtractor;
    }

    public Optional<Transform> findTransform(
        Class<?> source,
        Class<?> destination
    ) {
        return transforms.stream()
            .filter(transform -> transform.getSourceType().equals(source))
            .filter(transform -> transform.getDestinationType().equals(destination))
            .findFirst();
    }

    public Optional<Mapping> findMapping(
        Class<?> source,
        Class<?> destination
    ) {
        return mappings.stream()
            .filter(mapping -> mapping.getSourceType().equals(source))
            .filter(mapping -> mapping.getDestinationType().equals(destination))
            .findFirst();
    }
}
