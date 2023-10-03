package easymapper;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

class TypeAnalyzer {

    public static Function<Parameter, Type> getParameterTypeResolver(Type type) {
        if (type instanceof Class<?>) {
            return Parameter::getType;
        } else if (type instanceof ParameterizedType) {
            return getParameterTypeResolver((ParameterizedType) type);
        } else {
            String message = "Cannot support parameter type resolver for the type: " + type;
            throw new RuntimeException(message);
        }
    }

    private static Function<Parameter, Type> getParameterTypeResolver(ParameterizedType type) {
        Map<TypeVariable<?>, Type> typeVariableMap = getTypeVariableMap(type);
        return parameter -> parameter.getParameterizedType() instanceof TypeVariable<?>
            ? typeVariableMap.get((TypeVariable<?>) parameter.getParameterizedType())
            : parameter.getType();
    }

    public static Function<Method, Type> getReturnTypeResolver(Type type) {
        if (type instanceof Class<?>) {
            return Method::getGenericReturnType;
        } else if (type instanceof ParameterizedType) {
            return getReturnTypeResolver((ParameterizedType) type);
        } else {
            String message = "Cannot support return type resolver for the type: " + type;
            throw new RuntimeException(message);
        }
    }

    private static Function<Method, Type> getReturnTypeResolver(ParameterizedType type) {
        Map<TypeVariable<?>, Type> typeVariableMap = getTypeVariableMap(type);
        return method -> method.getGenericReturnType() instanceof TypeVariable<?>
            ? typeVariableMap.get((TypeVariable<?>) method.getGenericReturnType())
            : method.getGenericReturnType();
    }

    private static Map<TypeVariable<?>, Type> getTypeVariableMap(ParameterizedType type) {
        TypeVariable<?>[] typeParameters = getTypeParameters(type);
        Type[] actualTypeArguments = type.getActualTypeArguments();
        Map<TypeVariable<?>, Type> typeVariableMap = new HashMap<>();
        for (int i = 0; i < typeParameters.length; i++) {
            typeVariableMap.put(typeParameters[i], actualTypeArguments[i]);
        }
        return Collections.unmodifiableMap(typeVariableMap);
    }

    private static TypeVariable<?>[] getTypeParameters(ParameterizedType type) {
        if (type.getRawType() instanceof Class<?>) {
            return ((Class<?>) type.getRawType()).getTypeParameters();
        } else {
            String message = "Cannot provide type parameters for the type: " + type;
            throw new RuntimeException(message);
        }
    }
}
