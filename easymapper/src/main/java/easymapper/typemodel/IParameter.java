package easymapper.typemodel;

import java.util.Optional;

public interface IParameter {

    IType getType();

    Optional<String> getName();
}
