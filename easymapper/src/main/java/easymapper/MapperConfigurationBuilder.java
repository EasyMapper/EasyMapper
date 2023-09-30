package easymapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static easymapper.Exceptions.argumentNullException;
import static java.util.Collections.unmodifiableCollection;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;

public final class MapperConfigurationBuilder {

    private ConstructorExtractor constructorExtractor;
    private final List<Transform> transforms;
    private final List<MappingBuilder<?, ?>> mappingBuilders;

    MapperConfigurationBuilder() {
        constructorExtractor = type -> Arrays.asList(type.getConstructors());
        transforms = initializeTransforms();
        mappingBuilders = new ArrayList<>();
    }

    private static List<Transform> initializeTransforms() {
        List<Transform> transforms = new ArrayList<>();

        addIdentityTransform(transforms, Boolean.class, boolean.class);
        addIdentityTransform(transforms, Byte.class, byte.class);
        addIdentityTransform(transforms, Short.class, short.class);
        addIdentityTransform(transforms, Integer.class, int.class);
        addIdentityTransform(transforms, Long.class, long.class);
        addIdentityTransform(transforms, Float.class, float.class);
        addIdentityTransform(transforms, Double.class, double.class);
        addIdentityTransform(transforms, Character.class, char.class);
        addIdentityTransform(transforms, UUID.class);
        addIdentityTransform(transforms, String.class);
        addIdentityTransform(transforms, BigInteger.class);
        addIdentityTransform(transforms, BigDecimal.class);
        addIdentityTransform(transforms, LocalDate.class);
        addIdentityTransform(transforms, LocalTime.class);

        transforms.add(
            Transform.create(
                UUID.class,
                String.class,
                x -> x == null ? null : x.toString()));

        return transforms;
    }

    private static void addIdentityTransform(
        Collection<Transform> transforms,
        Class<?> sourceType,
        Class<?> destinationType
    ) {
        transforms.add(new Transform(sourceType, destinationType, identity()));
    }

    private static void addIdentityTransform(
        Collection<Transform> transforms,
        Class<?> type
    ) {
        addIdentityTransform(transforms, type, type);
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

    public <S, D> MapperConfigurationBuilder addTransform(
        Class<S> sourceType,
        Class<D> destinationType,
        Function<S, D> function
    ) {
        if (sourceType == null) {
            throw argumentNullException("sourceType");
        }

        if (destinationType == null) {
            throw argumentNullException("destinationType");
        }

        if (function == null) {
            throw argumentNullException("function");
        }

        transforms.stream()
            .filter(t -> t.getSourceType().equals(sourceType))
            .filter(t -> t.getDestinationType().equals(destinationType))
            .findFirst()
            .ifPresent(transforms::remove);

        transforms.add(Transform.create(sourceType, destinationType, function));

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

        mappingBuilders.stream()
            .filter(m -> m.getSourceType().equals(sourceType))
            .filter(m -> m.getDestinationType().equals(destinationType))
            .findFirst()
            .ifPresent(mappingBuilders::remove);

        mappingBuilders.add(builder);

        return this;
    }

    public Collection<Transform> getTransforms() {
        return unmodifiableCollection(new ArrayList<>(transforms));
    }

    Collection<Mapping> getMappings() {
        return unmodifiableCollection(new ArrayList<>(mappingBuilders)
            .stream()
            .map(MappingBuilder::build)
            .collect(toList()));
    }
}
