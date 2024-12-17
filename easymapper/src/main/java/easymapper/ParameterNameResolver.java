package easymapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Optional;

import lombok.NonNull;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@FunctionalInterface
public interface ParameterNameResolver {

    Optional<String> tryResolveName(Parameter parameter);

    default Optional<String[]> tryResolveNames(
        @NonNull Constructor<?> constructor
    ) {
        List<String> names = stream(constructor.getParameters())
            .map(this::tryResolveName)
            .map(x -> x.orElse(null))
            .collect(toList());

        return names.contains(null)
            ? Optional.empty()
            : Optional.of(names.toArray(new String[0]));
    }
}
