package easymapper;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class Mapping {

    private final Class<?> sourceType;
    private final Class<?> destinationType;
    private final Map<String, Function<Object, Object>> calculators;

    Mapping(
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

    public Optional<Object> tryCalculate(
        Object source,
        String destinationPropertyName
    ) {
        return Optional
            .ofNullable(calculators.get(destinationPropertyName))
            .map(calculator -> calculator.apply(source));
    }
}
