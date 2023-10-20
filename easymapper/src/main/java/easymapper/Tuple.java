package easymapper;

import java.util.Map;

class Tuple {

    private final Map<String, Object> properties;

    public Tuple(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Object get(String name) {
        return properties.get(name);
    }
}
