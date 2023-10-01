package easymapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static easymapper.Exceptions.argumentNullException;
import static java.util.Collections.unmodifiableList;
import static java.util.function.Function.identity;

public final class MapperConfiguration {

    private static final ConstructorExtractor defaultConstructorExtractor =
        t -> Arrays.asList(t.getConstructors());

    private static final ParameterNameResolver defaultParameterNameResolver =
        p -> p.isNamePresent() ? Optional.of(p.getName()) : Optional.empty();

    private static final Function<Object, Object> identity = identity();

    private ConstructorExtractor constructorExtractor;
    private ParameterNameResolver parameterNameResolver;
    private final List<Transform> transforms;
    private final List<Transform> unmodifiableTransforms;
    private final List<MappingBuilder<?, ?>> mappings;
    private final List<MappingBuilder<?, ?>> unmodifiableMappings;

    MapperConfiguration() {
        constructorExtractor = defaultConstructorExtractor;
        parameterNameResolver = defaultParameterNameResolver;
        transforms = initializeTransforms();
        unmodifiableTransforms = unmodifiableList(transforms);
        mappings = new ArrayList<>();
        unmodifiableMappings = unmodifiableList(mappings);
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
        addIdentityTransform(transforms, LocalDateTime.class);

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
        transforms.add(new Transform(sourceType, destinationType, identity));
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

    public MapperConfiguration setConstructorExtractor(ConstructorExtractor value) {
        if (value == null) {
            throw argumentNullException("value");
        }

        this.constructorExtractor = value;

        return this;
    }

    public ParameterNameResolver getParameterNameResolver() {
        return parameterNameResolver;
    }

    public MapperConfiguration setParameterNameResolver(ParameterNameResolver value) {
        if (value == null) {
            throw argumentNullException("value");
        }

        this.parameterNameResolver = value;

        return this;
    }

    public <S, D> MapperConfiguration addTransform(
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

    public <S, D> MapperConfiguration addMapping(
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

        mappings.stream()
            .filter(m -> m.getSourceType().equals(sourceType))
            .filter(m -> m.getDestinationType().equals(destinationType))
            .findFirst()
            .ifPresent(mappings::remove);

        mappings.add(builder);

        return this;
    }

    public Collection<Transform> getTransforms() {
        return unmodifiableTransforms;
    }

    public Collection<MappingBuilder<?, ?>> getMappings() {
        return unmodifiableMappings;
    }
}
