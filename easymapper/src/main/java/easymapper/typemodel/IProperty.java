package easymapper.typemodel;

import static java.util.Optional.empty;

import java.util.Optional;
import java.util.function.Supplier;

public interface IProperty {

    IType getType();

    String getName();

    boolean isGettable();

    Object getValue(Object instance);

    boolean isSettable();

    void setValue(Object instance, Object value);

    default Optional<Supplier<Object>> tryGetValue(Object instance) {
        if (isGettable()) {
            Object nullable = getValue(instance);
            return Optional.of(() -> nullable);
        } else {
            return empty();
        }
    }

    default boolean trySetValue(Object instance, Object value) {
        if (isSettable()) {
            setValue(instance, value);
            return true;
        } else {
            return false;
        }
    }
}
