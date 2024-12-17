package easymapper;

import java.lang.reflect.Type;
import java.util.function.BiConsumer;
import java.util.function.Function;

import lombok.AllArgsConstructor;

@AllArgsConstructor
final class Property extends ValueContainer {

    private final Type type;
    private final String name;
    private final Function<Object, Object> getter;
    private final BiConsumer<Object, Object> setter;

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

    public Property withHeadTruncatedName(int length) {
        return new Property(type, name().substring(length), getter, setter);
    }

    public boolean nameStartsWithIgnoreCase(String prefix) {
        return name.toLowerCase().startsWith(prefix.toLowerCase());
    }
}
