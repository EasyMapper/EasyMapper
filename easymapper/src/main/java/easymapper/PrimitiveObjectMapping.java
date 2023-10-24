package easymapper;

class PrimitiveObjectMapping {

    public static void configure(MapperConfiguration config) {
        configure(config, boolean.class);
        configure(config, Boolean.class);
        configure(config, boolean.class, Boolean.class);
        configure(config, Boolean.class, boolean.class);

        configure(config, byte.class);
        configure(config, Byte.class);
        configure(config, byte.class, Byte.class);
        configure(config, Byte.class, byte.class);

        configure(config, short.class);
        configure(config, Short.class);
        configure(config, short.class, Short.class);
        configure(config, Short.class, short.class);

        configure(config, int.class);
        configure(config, Integer.class);
        configure(config, int.class, Integer.class);
        configure(config, Integer.class, int.class);

        configure(config, long.class);
        configure(config, Long.class);
        configure(config, long.class, Long.class);
        configure(config, Long.class, long.class);

        configure(config, float.class);
        configure(config, Float.class);
        configure(config, float.class, Float.class);
        configure(config, Float.class, float.class);

        configure(config, double.class);
        configure(config, Double.class);
        configure(config, double.class, Double.class);
        configure(config, Double.class, double.class);

        configure(config, char.class);
        configure(config, Character.class);
        configure(config, char.class, Character.class);
        configure(config, Character.class, char.class);
    }

    private static <T> void configure(
        MapperConfiguration config,
        Class<T> type
    ) {
        configure(config, type, type);
    }

    @SuppressWarnings("unchecked")
    private static <S, D> void configure(
        MapperConfiguration config,
        Class<S> sourceType,
        Class<D> destinationType
    ) {
        config.map(
            sourceType,
            destinationType,
            mapping -> mapping.convert(context -> source -> (D) source));
    }
}
