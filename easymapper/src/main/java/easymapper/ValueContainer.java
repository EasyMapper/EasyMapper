package easymapper;

import java.lang.reflect.Type;

abstract class ValueContainer {

    public abstract Type type();

    public abstract String name();

    public abstract boolean isReadable();

    public abstract boolean isWritable();

    public final boolean isReadOnly() {
        return isReadable() && isWritable() == false;
    }

    protected final void assertThatWritable() {
        if (isReadOnly()) {
            throw new UnsupportedOperationException(
                "'" + name() + "' is read-only.");
        }
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
