package easymapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static easymapper.Exceptions.argumentNullException;
import static java.util.Collections.unmodifiableCollection;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;

public final class MapperConfigurationBuilder {

    private ConstructorExtractor constructorExtractor;
    private final List<MappingBuilder<?, ?>> mappingBuilders;

    MapperConfigurationBuilder() {
        constructorExtractor = type -> Arrays.asList(type.getConstructors());
        mappingBuilders = new ArrayList<>();
    }

    public ConstructorExtractor getConstructorExtractor() {
        return constructorExtractor;
    }

    public MapperConfigurationBuilder setConstructorExtractor(ConstructorExtractor value) {
        if (value == null) {
            throw argumentNullException("value");
        }

        this.constructorExtractor = value;
        return this;
    }

    public <S, D> MapperConfigurationBuilder addMapping(
        Class<S> sourceType,
        Class<D> destinationType,
        Consumer<MappingBuilder<S, D>> configurer
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

        MappingBuilder<S, D> builder = new MappingBuilder<>(sourceType, destinationType);
        configurer.accept(builder);
        this.mappingBuilders.add(builder);

        return this;
    }

    public Collection<Transform> getTransforms() {
        Collection<Transform> transforms = new ArrayList<>();

        addIdentityTransform(transforms, boolean.class);
        addIdentityTransform(transforms, byte.class);
        addIdentityTransform(transforms, short.class);
        addIdentityTransform(transforms, int.class);
        addIdentityTransform(transforms, long.class);
        addIdentityTransform(transforms, float.class);
        addIdentityTransform(transforms, double.class);
        addIdentityTransform(transforms, char.class);
        addIdentityTransform(transforms, UUID.class);
        addIdentityTransform(transforms, String.class);

        transforms.add(Transform.create(UUID.class, String.class, UUID::toString));

        return transforms;
    }

    private static void addIdentityTransform(
        Collection<Transform> transforms,
        Class<?> type
    ) {
        transforms.add(new Transform(type, type, identity()));
    }

    Collection<Mapping> getMappings() {
        return unmodifiableCollection(mappingBuilders
            .stream()
            .map(MappingBuilder::build)
            .collect(toList()));
    }
}
