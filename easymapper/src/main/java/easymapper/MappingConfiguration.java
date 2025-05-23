package easymapper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Accessors(fluent = true)
class MappingConfiguration {

    private final ConstructorExtractor constructorExtractor;
    private final ParameterNameResolver parameterNameResolver;
    private final ConverterContainer converters;
    private final ProjectorContainer projectors;
    private final ExtractorContainer extractors;

    public static MappingConfiguration build(
        MapperConfigurationBuilder config
    ) {
        return new MappingConfiguration(
            config.constructorExtractor(),
            config.parameterNameResolver(),
            config.converters().build(),
            config.projectors().build(),
            config.extractors().build()
        );
    }
}
