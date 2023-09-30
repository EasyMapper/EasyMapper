package easymapper;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static easymapper.Exceptions.argumentNullException;
import static java.util.Collections.unmodifiableList;

public class CompositeParameterNameResolver implements ParameterNameResolver {

    private final List<ParameterNameResolver> resolvers;

    public CompositeParameterNameResolver(ParameterNameResolver... resolvers) {
        if (resolvers == null) {
            throw argumentNullException("resolvers");
        }

        for (ParameterNameResolver resolver : resolvers) {
            if (resolver == null) {
                String message = "resolvers cannot contain null";
                throw new IllegalArgumentException(message);
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
