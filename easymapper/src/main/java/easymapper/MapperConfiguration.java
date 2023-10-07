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
import java.util.function.BiFunction;
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

    private ConstructorExtractor constructorExtractor;
    private ParameterNameResolver parameterNameResolver;
    private final List<Converter> converters;
    private final List<Converter> unmodifiableConverters;
    private final List<MappingBuilder<?, ?>> mappings;
    private final List<MappingBuilder<?, ?>> unmodifiableMappings;

    MapperConfiguration() {
        constructorExtractor = defaultConstructorExtractor;
        parameterNameResolver = defaultParameterNameResolver;
        converters = initializeTransforms();
        unmodifiableConverters = unmodifiableList(converters);
        mappings = new ArrayList<>();
        unmodifiableMappings = unmodifiableList(mappings);
    }

    private static List<Converter> initializeTransforms() {
        List<Converter> converters = new ArrayList<>();

        addIdentityConverter(converters, boolean.class, boolean.class);
        addIdentityConverter(converters, byte.class, byte.class);
        addIdentityConverter(converters, short.class, short.class);
        addIdentityConverter(converters, int.class, int.class);
        addIdentityConverter(converters, long.class, long.class);
        addIdentityConverter(converters, float.class, float.class);
        addIdentityConverter(converters, double.class, double.class);
        addIdentityConverter(converters, char.class, char.class);
        addIdentityConverter(converters, UUID.class);
        addIdentityConverter(converters, String.class);
        addIdentityConverter(converters, BigInteger.class);
        addIdentityConverter(converters, BigDecimal.class);
        addIdentityConverter(converters, LocalDate.class);
        addIdentityConverter(converters, LocalTime.class);
        addIdentityConverter(converters, LocalDateTime.class);

        converters.add(
            Converter.create(
                UUID.class,
                String.class,
                (source, context) -> source == null ? null : source.toString()));

        converters.add(CollectionMapping.CONVERTER);

        return converters;
    }

    private static void addIdentityConverter(
        Collection<Converter> transforms,
        Class<?> sourceType,
        Class<?> destinationType
    ) {
        transforms.add(
            new Converter(
                type -> type.equals(sourceType),
                type -> type.equals(destinationType),
                identity()));
    }

    private static void addIdentityConverter(
        Collection<Converter> transforms,
        Class<?> type
    ) {
        addIdentityConverter(transforms, type, type);
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
        BiFunction<S, ConversionContext, D> function
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

        converters.add(Converter.create(sourceType, destinationType, function));

        return this;
    }

    public <S, D> MapperConfiguration addTransform(
        Class<S> sourceType,
        Class<D> destinationType,
        Function<S, D> function
    ) {
        if (function == null) {
            throw argumentNullException("function");
        }

        return addTransform(
            sourceType,
            destinationType,
            (s, c) -> function.apply(s));
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

        mappings.add(builder);

        return this;
    }

    public Collection<Converter> getConverters() {
        return unmodifiableConverters;
    }

    public Collection<MappingBuilder<?, ?>> getMappings() {
        return unmodifiableMappings;
    }
}
