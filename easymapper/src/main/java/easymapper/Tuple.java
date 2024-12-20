package easymapper;

import java.util.Map;

import lombok.AllArgsConstructor;

@AllArgsConstructor
class Tuple {

    private final Map<String, Object> values;

    public Object get(String name) {
        return values.get(name);
    }
}
