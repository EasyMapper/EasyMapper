package easymapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class MappingBuilder<S, D> {

    private final Class<S> sourceType;
    private final Class<D> destinationType;
    private final Map<String, Function<Object, Object>> calculators;

    MappingBuilder(Class<S> sourceType, Class<D> destinationType) {
        this.sourceType = sourceType;
        this.destinationType = destinationType;
        this.calculators = new HashMap<>();
    }

    public Class<S> getSourceType() {
        return sourceType;
    }

    public Class<D> getDestinationType() {
        return destinationType;
    }

    public MappingBuilder<S, D> set(
        String destinationPropertyName,
        Function<S, Object> calculator
    ) {
        if (destinationPropertyName == null) {
            throw Exceptions.argumentNullException("destinationPropertyName");
        }

        if (calculator == null) {
            throw Exceptions.argumentNullException("calculator");
        }

        if (calculators.containsKey(destinationPropertyName)) {
            throw new IllegalArgumentException("Destination property already mapped");
        }

        calculators.put(
            destinationPropertyName,
            x -> calculator.apply(sourceType.cast(x)));

        return this;
    }

    Mapping build() {
        return new Mapping(sourceType, destinationType, calculators);
    }
}
