package easymapper;

import java.lang.reflect.Type;
import java.util.function.BiConsumer;
import java.util.function.Function;

final class Property {

    private final Type type;
    private final String name;
    private final Function<Object, Object> getter;
    private final BiConsumer<Object, Object> setter;

    public Property(
        Type type,
        String name,
        Function<Object, Object> getter,
        BiConsumer<Object, Object> setter
    ) {
        this.type = type;
        this.name = name;
        this.getter = getter;
        this.setter = setter;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Object getValue(Object instance) {
        return getter.apply(instance);
    }

    public boolean isSettable() {
        return setter != null;
    }

    public void setValue(Object instance, Object value) {
        setter.accept(instance, value);
    }

    @Override
    public String toString() {
        return type.getTypeName() + " " + name;
    }
}
