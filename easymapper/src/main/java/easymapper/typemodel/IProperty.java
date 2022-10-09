package easymapper.typemodel;

public interface IProperty {

    IType getType();

    String getName();

    boolean isGettable();

    Object getValue(Object instance);

    boolean isSettable();

    void setValue(Object instance, Object value);
}
