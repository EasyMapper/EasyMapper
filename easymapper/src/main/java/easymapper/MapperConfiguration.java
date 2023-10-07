package easymapper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static easymapper.Exceptions.argumentNullException;
import static java.util.Collections.unmodifiableList;

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
        converters = new ArrayList<>();
        unmodifiableConverters = unmodifiableList(converters);
        mappings = new ArrayList<>();
        unmodifiableMappings = unmodifiableList(mappings);
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
        BiFunction<S, ConversionContext, D> function
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
        BiFunction<Object, ConversionContext, Object> function
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

    public MapperConfiguration apply(Consumer<MapperConfiguration> configurer) {
        if (configurer == null) {
            throw argumentNullException("configurer");
        }

        configurer.accept(this);

        return this;
    }

    public Collection<Converter> getConverters() {
        return unmodifiableConverters;
    }

    public Collection<MappingBuilder<?, ?>> getMappings() {
        return unmodifiableMappings;
    }
}
