package easymapper;

import java.util.UUID;

class UUIDMapping {

    public static void configure(MapperConfiguration config) {
        config.addConverter(
            UUID.class,
            String.class,
            (context, source) -> asString(source)
        );
    }

    private static String asString(UUID source) {
        return source == null ? null : source.toString();
    }
}
