package easymapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static easymapper.Exceptions.argumentNullException;
import static java.util.Collections.unmodifiableCollection;
import static java.util.stream.Collectors.toList;

public final class MapperConfigurationBuilder {

    private final List<MappingBuilder<?, ?>> mappingBuilders;

    MapperConfigurationBuilder() {
        this.mappingBuilders = new ArrayList<>();
    }

    public <T, S> void addMapping(
        Class<T> sourceType,
        Class<S> destinationType,
        Consumer<MappingBuilder<T, S>> configurer
    ) {
        if (sourceType == null) {
            throw argumentNullException("sourceType");
        }

        if (destinationType == null) {
            throw argumentNullException("destinationType");
        }

        if (configurer == null) {
            throw argumentNullException("configurer");
        }

        MappingBuilder<T, S> builder = new MappingBuilder<>(sourceType, destinationType);
        configurer.accept(builder);
        this.mappingBuilders.add(builder);
    }

    Collection<Mapping> getMappings() {
        return unmodifiableCollection(mappingBuilders
            .stream()
            .map(MappingBuilder::build)
            .collect(toList()));
    }
}
