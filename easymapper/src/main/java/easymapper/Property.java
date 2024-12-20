package easymapper;

import java.lang.reflect.Type;
import java.util.function.BiConsumer;
import java.util.function.Function;

import lombok.AllArgsConstructor;

@AllArgsConstructor
final class Property {

    private final Type type;
    private final String name;
    private final Function<Object, Object> getter;
    private final BiConsumer<Object, Object> setter;

    public Type type() {
        return type;
    }

    public String name() {
        return name;
    }

    public boolean isReadable() {
        return getter != null;
    }

    public boolean isWritable() {
        return setter != null;
    }

    public boolean isReadOnly() {
        return isReadable() && isWritable() == false;
    }

    public Object get(Object instance) {
        return getter.apply(instance);
    }

    public void set(Object instance, Object value) {
        assertThatWritable();
        setter.accept(instance, value);
    }

    private void assertThatWritable() {
        if (isReadOnly()) {
            String message = "'" + name() + "' is read-only.";
            throw new UnsupportedOperationException(message);
        }
    }

    public Property withHeadTruncatedName(int length) {
        return new Property(type, name().substring(length), getter, setter);
    }

    public boolean nameStartsWithIgnoreCase(String prefix) {
        return name.toLowerCase().startsWith(prefix.toLowerCase());
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(type().getTypeName())
            .append(" ")
            .append(name())
            .append(" {");

        if (isReadable()) {
            s.append(" get;");
        }

        if (isWritable()) {
            s.append(" set;");
        }

        s.append(" }");

        return s.toString();
    }
}
