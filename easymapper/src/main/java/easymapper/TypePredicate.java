package easymapper;

import java.lang.reflect.Type;

@FunctionalInterface
public interface TypePredicate {

    boolean test(Type type);

    TypePredicate ACCEPT_ALL_TYPES = type -> true;
}
