package easymapper;

import java.util.UUID;

class UUIDMapping {

    public static void configurer(MapperConfiguration config) {
        config.addConverter(
            UUID.class,
            String.class,
            source -> context -> source == null ? null : source.toString());
    }
}
