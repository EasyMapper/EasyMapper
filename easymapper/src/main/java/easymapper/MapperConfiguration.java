package easymapper;

import java.lang.reflect.Type;
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

    private ConstructorExtractor constructorExtractor;
    private ParameterNameResolver parameterNameResolver;
    private final List<Converter> converters;
    private final List<Converter> unmodifiableConverters;
    private final List<MappingBuilder<?, ?>> mappings;
    private final List<MappingBuilder<?, ?>> unmodifiableMappings;

    MapperConfiguration() {
        constructorExtractor = defaultConstructorExtractor;
        parameterNameResolver = defaultParameterNameResolver;
        converters = initializeConverters();
        unmodifiableConverters = unmodifiableList(converters);
        mappings = new ArrayList<>();
        unmodifiableMappings = unmodifiableList(mappings);
    }

    private static List<Converter> initializeConverters() {
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
                type -> type.equals(UUID.class),
                type -> type.equals(String.class),
                (source, context) -> source == null ? null : source.toString()));

        converters.add(CollectionMapping.CONVERTER);

        return converters;
    }

    private static void addIdentityConverter(
        Collection<Converter> converters,
        Class<?> sourceType,
        Class<?> destinationType
    ) {
        converters.add(
            new Converter(
                type -> type.equals(sourceType),
                type -> type.equals(destinationType),
                identity()));
    }

    private static void addIdentityConverter(
        Collection<Converter> converters,
        Class<?> type
    ) {
        addIdentityConverter(converters, type, type);
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

    public <S, D> MapperConfiguration addConverter(
        Class<S> sourceType,
        Class<D> destinationType,
        ConverterFunction<S, D> function
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

        Converter converter = Converter.create(
            type -> type.equals(sourceType),
            type -> type.equals(destinationType),
            function);

        converters.add(converter);

        return this;
    }

    public <S, D> MapperConfiguration addConverter(
        TypeReference<S> sourceTypeReference,
        TypeReference<D> destinationTypeReference,
        ConverterFunction<S, D> function
    ) {
        if (sourceTypeReference == null) {
            throw argumentNullException("sourceTypeReference");
        }

        if (destinationTypeReference == null) {
            throw argumentNullException("destinationTypeReference");
        }

        if (function == null) {
            throw argumentNullException("function");
        }

        Type sourceType = sourceTypeReference.getType();
        Type destinationType = destinationTypeReference.getType();

        Converter converter = Converter.create(
            type -> type.equals(sourceType),
            type -> type.equals(destinationType),
            function);

        converters.add(converter);

        return this;
    }

    public MapperConfiguration addConverter(
        Function<Type, Boolean> sourceTypePredicate,
        Function<Type, Boolean> destinationTypePredicate,
        ConverterFunction<Object, Object> function
    ) {
        if (sourceTypePredicate == null) {
            throw argumentNullException("sourceTypePredicate");
        }

        if (destinationTypePredicate == null) {
            throw argumentNullException("destinationTypePredicate");
        }

        if (function == null) {
            throw argumentNullException("function");
        }

        Converter converter = Converter.create(
            sourceTypePredicate,
            destinationTypePredicate,
            function);

        converters.add(converter);

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
