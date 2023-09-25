package easymapper;

import java.util.Map;
import java.util.Optional;

public class Mapping {

    private final Class<?> sourceType;
    private final Class<?> destinationType;
    private final Map<String, String> map;

    Mapping(
        Class<?> sourceType,
        Class<?> destinationType,
        Map<String, String> map
    ) {
        this.sourceType = sourceType;
        this.destinationType = destinationType;
        this.map = map;
    }

    public Class<?> getSourceType() {
        return sourceType;
    }

    public Class<?> getDestinationType() {
        return destinationType;
    }

    public Optional<String> getSourcePropertyName(String destinationPropertyName) {
        return Optional.ofNullable(map.get(destinationPropertyName));
    }
}
