package cleanpojo.japper;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Mapper {

    public <T> T map(Object source, Class<T> destinationType) {
        if (source == null) {
            return null;
        }

        Constructor<?> constructor = getConstructor(destinationType);
        Parameter[] parameters = constructor.getParameters();
        Object[] arguments = new Object[parameters.length];
        Map<String, Property> sourceProperties = Property.getProperties(source);
        String[] destinationPropertyNames = getPropertyNames(constructor);
        for (int i = 0; i < parameters.length; i++) {
            String destinationPropertyName = destinationPropertyNames[i];
            Property sourceProperty = sourceProperties.get(destinationPropertyName);
            Object sourcePropertyValue = sourceProperty.getValue(source);
            Parameter parameter = parameters[i];
            if (parameter.getType().isPrimitive() || parameter.getType().equals(String.class)
                    || parameter.getType().equals(UUID.class)) {
                arguments[i] = sourcePropertyValue;
            } else {
                arguments[i] = map(sourcePropertyValue, parameter.getType());
            }
        }

        Object destination = createInstance(constructor, arguments);

        Map<String, Property> destinationProperties = Property.getProperties(destination);
        for (String propertyName : destinationProperties.keySet()) {
            if (sourceProperties.containsKey(propertyName)) {
                Object propertyValue = sourceProperties.get(propertyName).getValue(source);
                destinationProperties.get(propertyName).setValueIfPossible(destination, propertyValue);
            }
        }

        return destinationType.cast(destination);
    }

    public <T> T map(Object source, T destination) {
        Map<String, Property> destinationProperties = Property.getProperties(destination);
        Map<String, Property> sourceProperties = Property.getProperties(source);
        for (String propertyName : destinationProperties.keySet()) {
            if (sourceProperties.containsKey(propertyName)) {
                Object propertyValue = sourceProperties.get(propertyName).getValue(source);
                destinationProperties.get(propertyName).setValueIfPossible(destination, propertyValue);
            }
        }

        return destination;
    }

    private <T> Constructor<?> getConstructor(Class<T> destinationType) {
        Constructor<?>[] constructors = destinationType.getConstructors();
        return constructors[0];
    }

    private String[] getPropertyNames(Constructor<?> constructor) {
        return constructor.getAnnotation(ConstructorProperties.class).value();
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

    private static final class Property {
        private String name;

        private Method getter;
        private Method setter;

        private Property(String name, Method getter, Method setter) {
            this.name = name;
            this.getter = getter;
            this.setter = setter;
        }

        public static Map<String, Property> getProperties(Object instance) {
            Method[] methods = instance.getClass().getMethods();

            Map<String, Method> getters = new HashMap<>();
            for (Method method : methods) {
                if (method.getName().startsWith("get") == false) {
                    continue;
                }
                String name = decapitalizeFirstCharacter(method.getName().substring(3));
                getters.put(name, method);
            }

            Map<String, Method> setters = new HashMap<>();
            for (Method method : methods) {
                if (method.getName().startsWith("set") == false) {
                    continue;
                }
                String name = decapitalizeFirstCharacter(method.getName().substring(3));
                setters.put(name, method);
            }

            return Stream.concat(getters.keySet().stream(), setters.keySet().stream())
                    .distinct()
                    .map(name -> new Property(
                            name,
                            getters.getOrDefault(name, null),
                            setters.getOrDefault(name, null)))
                    .collect(Collectors.toMap(x -> x.getName(), x -> x));
        }

        private static String decapitalizeFirstCharacter(String s) {
            char firstCharacter = s.charAt(0);
            if (Character.isUpperCase(firstCharacter)) {
                return Character.toLowerCase(firstCharacter) + s.substring(1);
            } else {
                return s;
            }
        }

        public String getName() {
            return name;
        }

        public Object getValue(Object instance) {
            try {
                return getter.invoke(instance);
            } catch (IllegalAccessException
                    | IllegalArgumentException
                    | InvocationTargetException exception) {
                throw new RuntimeException(exception);
            }
        }

        public void setValueIfPossible(Object instance, Object value) {
            if (setter == null) {
                return;
            }

            try {
                setter.invoke(instance, value);
            } catch (IllegalAccessException
                    | IllegalArgumentException
                    | InvocationTargetException exception) {
                throw new RuntimeException(exception);
            }
        }
    }
}
