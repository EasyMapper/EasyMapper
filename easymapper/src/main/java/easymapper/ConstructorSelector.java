package easymapper;

import java.lang.reflect.Constructor;
import java.util.Arrays;

final class ConstructorSelector {

    static <T> Constructor<?> getConstructor(Class<T> destinationType) {
        return Arrays.stream(destinationType.getConstructors())
                .sorted((x, y) -> y.getParameterCount() - x.getParameterCount())
                .findFirst().get();
    }

}
