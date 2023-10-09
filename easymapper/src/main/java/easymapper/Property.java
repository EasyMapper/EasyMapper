package easymapper;

import java.lang.reflect.Type;
import java.util.function.BiConsumer;
import java.util.function.Function;

final class Property extends ValueContainer {

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

    @Override
    public Type type() {
        return type;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean isReadable() {
        return getter != null;
    }

    @Override
    public boolean isWritable() {
        return setter != null;
    }

    public Object get(Object instance) {
        return getter.apply(instance);
    }

    public void set(Object instance, Object value) {
        assertThatWritable();
        setter.accept(instance, value);
    }

    public Variable bind(Object instance) {
        return new Variable(
            type,
            name,
            () -> get(instance),
            value -> set(instance, value));
    }
}
