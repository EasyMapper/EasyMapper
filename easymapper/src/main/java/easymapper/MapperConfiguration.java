package easymapper;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

public final class MapperConfiguration {

    private final ConstructorExtractor constructorExtractor;
    private final Collection<Mapping> mappings;

    private MapperConfiguration(
        ConstructorExtractor constructorExtractor,
        Collection<Mapping> mappings
    ) {
        this.constructorExtractor = constructorExtractor;
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
            builder.getMappings()
        );
    }

    public ConstructorExtractor getConstructorExtractor() {
        return constructorExtractor;
    }

    public Optional<Mapping> getMapping(Class<?> source, Class<?> target) {
        return mappings.stream()
            .filter(mapping -> mapping.getSourceType().equals(source))
            .filter(mapping -> mapping.getDestinationType().equals(target))
            .findFirst();
    }
}
