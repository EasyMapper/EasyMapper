package easymapper;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class PropertyMapping {

    private final Class<?> sourceType;
    private final Class<?> destinationType;
    private final Map<String, Function<Object, Object>> calculators;

    PropertyMapping(
        Class<?> sourceType,
        Class<?> destinationType,
        Map<String, Function<Object, Object>> calculators
    ) {
        this.sourceType = sourceType;
        this.destinationType = destinationType;
        this.calculators = calculators;
    }

    public Class<?> getSourceType() {
        return sourceType;
    }

    public Class<?> getDestinationType() {
        return destinationType;
    }

    Map<String, Function<Object, Object>> getCalculators() {
        return calculators;
    }

    public Optional<Function<Object, Object>> findCalculator(
        String destinationPropertyName
    ) {
        return Optional.ofNullable(calculators.get(destinationPropertyName));
    }
}
