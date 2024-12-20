package easymapper;

import java.lang.reflect.Type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
class TypeMatchingEntry<V> {

    private final TypePredicate sourceTypePredicate;
    private final TypePredicate targetTypePredicate;
    private final V value;

    public boolean match(Type sourceType, Type targetType) {
        return sourceTypePredicate.test(sourceType)
            && targetTypePredicate.test(targetType);
    }
}
