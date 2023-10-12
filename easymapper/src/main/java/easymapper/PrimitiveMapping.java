package easymapper;

import java.util.function.Function;

class PrimitiveMapping {

    public static void configure(MapperConfiguration config) {
        identity(config, boolean.class);
        identity(config, byte.class);
        identity(config, short.class);
        identity(config, int.class);
        identity(config, long.class);
        identity(config, float.class);
        identity(config, double.class);
        identity(config, char.class);
    }

    private static void identity(MapperConfiguration config, Class<?> type) {
        config.map(type::equals, type::equals,
            mapping -> mapping.convert(context -> Function.identity()));
    }
}
