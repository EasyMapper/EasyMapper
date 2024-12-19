package easymapper;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import static easymapper.Collections.copyInReverseOrder;
import static java.util.stream.Collectors.toList;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Accessors(fluent = true)
class MappingSettings {

    private final ConstructorExtractor constructorExtractor;
    private final ParameterNameResolver parameterNameResolver;
    private final Converters converters;
    private final Projectors projectors;
    private final List<Mapping<Object, Object>> mappings;

    public static MappingSettings from(MapperConfiguration config) {
        return new MappingSettings(
            config.constructorExtractor(),
            config.parameterNameResolver(),
            config.converters(),
            config.projectors(),
            buildMappings(config)
        );
    }

    @SuppressWarnings("unchecked")
    private static List<Mapping<Object, Object>> buildMappings(
        MapperConfiguration config
    ) {
        return copyInReverseOrder(config
            .getMappings()
            .stream()
            .map(MappingBuilder::build)
            .map(mapping -> (Mapping<Object, Object>) mapping)
            .collect(toList()));
    }
}
