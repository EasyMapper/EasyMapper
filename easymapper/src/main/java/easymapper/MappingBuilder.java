package easymapper;

import java.util.HashMap;
import java.util.Map;

public final class MappingBuilder<T, S> {

    private final Class<T> sourceType;
    private final Class<S> destinationType;
    private final Map<String, String> map = new HashMap<>();

    MappingBuilder(Class<T> sourceType, Class<S> destinationType) {
        this.sourceType = sourceType;
        this.destinationType = destinationType;
    }

    public MappingBuilder<T, S> map(
        String sourcePropertyName,
        String destinationPropertyName
    ) {
        if (sourcePropertyName == null) {
            throw Exceptions.argumentNullException("sourcePropertyName");
        }

        if (destinationPropertyName == null) {
            throw Exceptions.argumentNullException("destinationPropertyName");
        }

        if (map.containsValue(sourcePropertyName)) {
            throw new IllegalArgumentException("Source property name already mapped");
        }

        if (map.containsKey(destinationPropertyName)) {
            throw new IllegalArgumentException("Destination property name already mapped");
        }

        map.put(destinationPropertyName, sourcePropertyName);
        return this;
    }

    Mapping build() {
        return new Mapping(sourceType, destinationType, map);
    }
}
