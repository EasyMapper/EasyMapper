package easymapper;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;

@AllArgsConstructor
class TupleType implements Type {

    private final Map<String, Type> types;

    public Map<String, Getter> getGetters() {
        return types
            .keySet()
            .stream()
            .map(name -> new Getter(
                types.get(name),
                name,
                instance -> ((Tuple) instance).get(name)))
            .collect(Collectors.toMap(Getter::name, getter -> getter));
    }
}
