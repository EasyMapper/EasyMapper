package easymapper;

import java.util.UUID;

class UUIDMapping {

    public static void configure(MapperConfiguration config) {
        config.map(UUID.class, String.class,
            mapping -> mapping
                .convert(context -> UUIDMapping::asString));
    }

    private static String asString(UUID source) {
        return source == null ? null : source.toString();
    }
}
