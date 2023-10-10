package easymapper;

import java.lang.reflect.Type;
import java.util.function.Function;

class TypePredicates {

    public static Function<Type, Boolean> acceptAllTypes = type -> true;
}
