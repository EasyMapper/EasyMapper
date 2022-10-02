package cleanpojo.japper;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.UUID;

public final class Mapper {

    public <T> T map(Object source, Class<T> destinationType) {
        Constructor<?> constructor = getConstructor(destinationType);
        Parameter[] parameters = constructor.getParameters();
        Object[] arguments = new Object[parameters.length];
        Class<? extends Object> sourceType = source.getClass();
        Method[] methods = sourceType.getMethods();
        String[] propertyNames = getPropertyNames(constructor);
        for (int i = 0; i < parameters.length; i++) {
            String propertyName = propertyNames[i];
            Object propertyValue = getPropertyValue(methods, source, propertyName);
            Parameter parameter = parameters[i];
            if (parameter.getType().isPrimitive() || parameter.getType().equals(String.class)
                    || parameter.getType().equals(UUID.class)) {
                arguments[i] = propertyValue;
            } else {
                arguments[i] = map(propertyValue, parameter.getType());
            }
        }
        Object instance = createInstance(constructor, arguments);
        return destinationType.cast(instance);
    }

    private <T> Constructor<?> getConstructor(Class<T> destinationType) {
        Constructor<?>[] constructors = destinationType.getConstructors();
        return constructors[0];
    }

    private String[] getPropertyNames(Constructor<?> constructor) {
        return constructor.getAnnotation(ConstructorProperties.class).value();
    }

    private Object getPropertyValue(
            Method[] methods,
            Object source,
            String propertyName) {
        for (int j = 0; j < methods.length; j++) {
            Method method = methods[j];
            String methodName = method.getName();
            if (methodName.equalsIgnoreCase("get" + propertyName)) {
                return getPropertyValue(source, method);
            }
        }
        throw new RuntimeException();
    }

    private Object getPropertyValue(Object source, Method getter) {
        try {
            return getter.invoke(source);
        } catch (IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }

    private Object createInstance(
            Constructor<?> constructor,
            Object[] arguments) {
        try {
            return constructor.newInstance(arguments);
        } catch (InstantiationException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }
}
