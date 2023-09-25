package easymapper;

import java.lang.reflect.Constructor;
import java.util.Collection;

public interface ConstructorExtractor {

    Collection<Constructor<?>> extract(Class<?> type);
}
