package easymapper;

import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.function.Supplier;

class VariableWrapper extends ValueContainer {

    private final Type type;
    private final String name;
    private final Supplier<Object> getter;
    private final Consumer<Object> setter;

    public VariableWrapper(
        Type type,
        String name,
        Supplier<Object> getter,
        Consumer<Object> setter
    ) {
        this.type = type;
        this.name = name;
        this.getter = getter;
        this.setter = setter;
    }

    public VariableWrapper(Type type, String name, Supplier<Object> getter) {
        this(type, name, getter, null);
    }

    public VariableWrapper(Type type, String name, Object value) {
        this(type, name, () -> value, null);
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

    public Object get() {
        return getter.get();
    }

    public void set(Object value) {
        assertThatWritable();

        setter.accept(value);
    }

    public Object getOrSetIfNull(Supplier<Object> valueSupplier) {
        assertThatWritable();

        Object value = get();

        if (value != null) {
            return value;
        }

        Object newValue = valueSupplier.get();
        set(newValue);
        return newValue;
    }

    private void assertThatWritable() {
        if (isReadOnly()) {
            throw new UnsupportedOperationException(
                "'" + name + "' is read-only.");
        }
    }
}
