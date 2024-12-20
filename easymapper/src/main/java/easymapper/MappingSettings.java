package easymapper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Accessors(fluent = true)
class MappingSettings {

    private final ConstructorExtractor constructorExtractor;
    private final ParameterNameResolver parameterNameResolver;
    private final ConverterSet converters;
    private final ProjectorSet projectors;
    private final ExtractorSet extractors;

    public static MappingSettings create(MapperConfiguration config) {
        return new MappingSettings(
            config.constructorExtractor(),
            config.parameterNameResolver(),
            config.converters().build(),
            config.projectors().build(),
            config.extractors().build()
        );
    }
}
