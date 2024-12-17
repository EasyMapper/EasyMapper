package easymapper;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.NonNull;

import static java.util.Collections.unmodifiableList;

public class CompositeParameterNameResolver implements ParameterNameResolver {

    private final List<ParameterNameResolver> resolvers;

    @SuppressWarnings("ConstantValue")
    public CompositeParameterNameResolver(
        @NonNull ParameterNameResolver... resolvers
    ) {
        for (ParameterNameResolver resolver : resolvers) {
            if (resolver == null) {
                String message = "resolvers cannot contain null";
                throw new NullPointerException(message);
            }
        }

        this.resolvers = unmodifiableList(Arrays.asList(resolvers));
    }

    @Override
    public Optional<String> tryResolveName(Parameter parameter) {
        for (ParameterNameResolver resolver : resolvers) {
            Optional<String> name = resolver.tryResolveName(parameter);
            if (name.isPresent()) {
                return name;
            }
        }

        return Optional.empty();
    }
}
