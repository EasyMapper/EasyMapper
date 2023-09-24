package easymapper;

import java.lang.reflect.Constructor;
import java.util.Arrays;

final class ConstructorSelector {

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    static <T> Constructor<?> getConstructor(Class<T> destinationType) {
        return Arrays
            .stream(destinationType.getConstructors())
            .min((x, y) -> y.getParameterCount() - x.getParameterCount())
            .get();
    }
}
