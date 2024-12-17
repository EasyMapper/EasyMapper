package easymapper;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;

class DefaultConstructorExtractor implements ConstructorExtractor {

    public static final DefaultConstructorExtractor INSTANCE = instance();

    private static DefaultConstructorExtractor instance() {
        return new DefaultConstructorExtractor();
    }

    @Override
    public Collection<Constructor<?>> extract(Class<?> type) {
        return Arrays.asList(type.getConstructors());
    }
}
