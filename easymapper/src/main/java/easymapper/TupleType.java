package easymapper;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Collectors;

class TupleType implements Type {

    private final Map<String, Type> properties;

    public TupleType(Map<String, Type> properties) {
        this.properties = properties;
    }

    public Map<String, Getter> getGetters() {
        return properties
            .keySet()
            .stream()
            .map(name -> new Getter(
                properties.get(name),
                name,
                instance -> ((Tuple) instance).get(name)))
            .collect(Collectors.toMap(Getter::name, getter -> getter));
    }
}
