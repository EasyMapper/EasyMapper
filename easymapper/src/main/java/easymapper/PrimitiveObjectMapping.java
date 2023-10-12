package easymapper;

import static java.util.function.Function.identity;

class PrimitiveObjectMapping {

    public static void configure(MapperConfiguration config) {
        configure(config, boolean.class);
        configure(config, byte.class);
        configure(config, short.class);
        configure(config, int.class);
        configure(config, long.class);
        configure(config, float.class);
        configure(config, double.class);
        configure(config, char.class);
    }

    private static <T> void configure(
        MapperConfiguration config,
        Class<T> type
    ) {
        config.map(type, type, mapping ->
            mapping.convert(context -> identity()));
    }
}
