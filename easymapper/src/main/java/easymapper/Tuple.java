package easymapper;

import java.util.Map;

import lombok.AllArgsConstructor;

@AllArgsConstructor
class Tuple {

    private final Map<String, Object> properties;

    public Object get(String name) {
        return properties.get(name);
    }
}
