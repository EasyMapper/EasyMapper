package easymapper;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

public final class MapperConfiguration {

    private final Collection<Mapping> mappings;
    private final ConstructorExtractor constructorExtractor;

    private MapperConfiguration(
        Collection<Mapping> mappings,
        ConstructorExtractor constructorExtractor
    ) {
        this.mappings = mappings;
        this.constructorExtractor = constructorExtractor;
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
            builder.getMappings(),
            builder.getConstructorExtractor());
    }

    public Optional<Mapping> getMapping(Class<?> source, Class<?> target) {
        return mappings.stream()
            .filter(mapping -> mapping.getSourceType().equals(source))
            .filter(mapping -> mapping.getDestinationType().equals(target))
            .findFirst();
    }

    public ConstructorExtractor getConstructorExtractor() {
        return constructorExtractor;
    }
}
