package easymapper;

import java.lang.reflect.Parameter;
import java.util.Optional;

@FunctionalInterface
public interface ParameterNameResolver {

    Optional<String> tryResolveName(Parameter parameter);
}
