package easymapper;

import java.lang.reflect.Parameter;
import java.util.Optional;

class DefaultParameterNameResolver implements ParameterNameResolver {

    public static final DefaultParameterNameResolver INSTANCE = instance();

    private static DefaultParameterNameResolver instance() {
        return new DefaultParameterNameResolver();
    }

    @Override
    public Optional<String> tryResolveName(Parameter parameter) {
        return parameter.isNamePresent()
            ? Optional.of(parameter.getName())
            : Optional.empty();
    }
}
