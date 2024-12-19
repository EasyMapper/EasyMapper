package easymapper;

import java.util.ArrayList;
import java.util.List;

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
    private final Converters converters;
    private final Projectors projectors;
    private final Extractors extractors;
    private final List<Mapping<Object, Object>> mappings;

    public static MappingSettings from(MapperConfiguration config) {
        return new MappingSettings(
            config.constructorExtractor(),
            config.parameterNameResolver(),
            config.converters(),
            config.projectors(),
            config.extractors(),
            new ArrayList<>()
        );
    }
}
