package easymapper;

import java.util.function.BiConsumer;
import java.util.function.Function;

final class Property {

    private final Class<?> type;
    private final String name;
    private final Function<Object, Object> getter;
    private final BiConsumer<Object, Object> setter;

    public Property(
        Class<?> type,
        String name,
        Function<Object, Object> getter,
        BiConsumer<Object, Object> setter
    ) {
        this.type = type;
        this.name = name;
        this.getter = getter;
        this.setter = setter;
    }

    public Class<?> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Object getValue(Object instance) {
        return getter.apply(instance);
    }

    public void setValueIfPossible(Object instance, Object value) {
        if (setter == null) {
            return;
        }

        setter.accept(instance, value);
    }
}
