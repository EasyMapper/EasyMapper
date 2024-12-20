package easymapper;

import java.util.UUID;

class UUIDMapping {

    public static void configure(MapperConfigurationBuilder config) {
        config.addConverter(
            UUID.class,
            String.class,
            (source, context) -> asString(source)
        );
    }

    private static String asString(UUID source) {
        return source == null ? null : source.toString();
    }
}
