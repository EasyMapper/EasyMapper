package easymapper.typemodel;

public interface IConstructor {

    IParameter[] getParameters();

    Object construct(Object... arguments);
}
