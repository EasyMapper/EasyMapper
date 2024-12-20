package easymapper;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static java.lang.System.lineSeparator;
import static java.util.Comparator.comparingInt;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public final class MappingContext {

    private final MappingSettings settings;

    @Getter(AccessLevel.PACKAGE)
    private final Type sourceType;

    @Getter(AccessLevel.PACKAGE)
    private final Type targetType;

    MappingContext branch(Type sourceType, Type targetType) {
        return new MappingContext(settings, sourceType, targetType);
    }

    Object convert(Object source) {
        return settings
            .converters()
            .find(sourceType, targetType)
            .map(converter -> converter.bindContext(this))
            .orElse(this::convertInDefaultWay)
            .apply(source);
    }

    private Object convertInDefaultWay(Object source) {
        return source == null ? null : constructThenProject(source);
    }

    private Object constructThenProject(Object source) {
        Object target = construct(source);
        project(source, target);
        return target;
    }

    private Object construct(Object source) {
        Constructor<?> constructor = getConstructor(targetType);
        Object[] arguments = buildArguments(source, constructor);
        return invoke(constructor, arguments);
    }

    private Constructor<?> getConstructor(Type type) {
        if (type instanceof ParameterizedType) {
            return getConstructor(((ParameterizedType) type).getRawType());
        } else if (type instanceof Class<?>) {
            return getConstructor((Class<?>) type);
        } else {
            throw new RuntimeException(composeConstructorNotFoundMessage(type));
        }
    }

    private static String composeConstructorNotFoundMessage(Type type) {
        String newLine = lineSeparator();
        return "Cannot provide constructor for the type: " + type
            + newLine + "If you use Mapper to convert instances of generic classes, use the TypeReference<T> interface to specify the generic type."
            + newLine
            + newLine + "For example,"
            + newLine
            + newLine + "mapper.convert("
            + newLine + "     source,"
            + newLine + "     new TypeReference<DomainEvent<OrderPlaced>>() {},"
            + newLine + "     new TypeReference<IntegrationEvent<OrderPlacedEvent>>() {});";
    }

    private Constructor<?> getConstructor(Class<?> type) {
        return settings
            .constructorExtractor()
            .extract(type)
            .stream()
            .max(comparingInt(Constructor::getParameterCount))
            .orElseThrow(() -> {
                String message = "No constructor found for " + type;
                return new RuntimeException(message);
            });
    }

    private Object[] buildArguments(Object source, Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        String[] propertyNames = getPropertyNames(constructor);
        Object[] arguments = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            arguments[i] = extractOrConvert(source, propertyNames[i]);
        }

        return arguments;
    }

    private String[] getPropertyNames(Constructor<?> constructor) {
        return settings
            .parameterNameResolver()
            .tryResolveNames(constructor)
            .orElseGet(() -> getAnnotatedPropertyNames(constructor));
    }

    private static String[] getAnnotatedPropertyNames(
        Constructor<?> constructor
    ) {
        ConstructorProperties annotation = constructor
            .getAnnotation(ConstructorProperties.class);

        if (annotation == null) {
            String message = "The constructor " + constructor
                + " is not decorated with @ConstructorProperties annotation.";
            throw new RuntimeException(message);
        } else {
            return annotation.value();
        }
    }

    private Object extractOrConvert(Object source, String propertyName) {
        return settings
            .extractors()
            .find(sourceType, targetType, propertyName)
            .map(extractor -> extractor.bindAll(this, source))
            .orElse(() -> convertProperty(source, propertyName))
            .get();
    }

    private Object convertProperty(Object source, String propertyName) {
        Property sourceProperty = Properties.get(sourceType).get(propertyName);
        Property targetProperty = Properties.get(targetType).get(propertyName);
        MappingContext context = branch(
            sourceProperty.type(),
            targetProperty.type()
        );
        return context.convert(sourceProperty.get(source));
    }

    private static Object invoke(
        Constructor<?> constructor,
        Object[] arguments
    ) {
        try {
            return constructor.newInstance(arguments);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    void project(Object source, Object target) {
        if (source == target) {
            return;
        }

        settings
            .projectors()
            .find(sourceType, targetType)
            .map(projector -> projector.bindContext(this))
            .orElse(this::projectInDefaultWay)
            .accept(source, target);
    }

    private void projectInDefaultWay(Object source, Object target) {
        setWritableProperties(source, target);
        projectToReadOnlyProperties(source, target);
    }

    private void setWritableProperties(Object source, Object target) {
        Properties targetProperties = Properties.get(targetType);
        targetProperties.useWritableProperties(targetProperty -> settings
            .extractors()
            .find(sourceType, targetType, targetProperty.name())
            .<Runnable>map(extractor -> () ->
                targetProperty.set(target, extractor.extract(source, this))
            )
            .orElse(() -> Properties.get(sourceType).ifPresent(
                targetProperty.name(),
                sourceProperty -> {
                    MappingContext context = branch(
                        sourceProperty.type(),
                        targetProperty.type()
                    );
                    Object sourceValue = sourceProperty.get(source);
                    if (sourceValue != targetProperty.get(target)) {
                        targetProperty.set(
                            target,
                            context.convert(sourceValue)
                        );
                    }
                }
            ))
            .run()
        );
    }

    private void projectToReadOnlyProperties(Object source, Object target) {
        Properties targetProperties = Properties.get(targetType);
        targetProperties.useReadOnlyProperties(targetProperty ->
            Properties.get(sourceType).ifPresent(
                targetProperty.name(),
                sourceProperty -> {
                    MappingContext context = branch(
                        sourceProperty.type(),
                        targetProperty.type()
                    );
                    context.project(
                        sourceProperty.get(source),
                        targetProperty.get(target)
                    );
                }
            )
        );
    }
}
