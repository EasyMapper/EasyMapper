package easymapper;

import java.util.UUID;

class UUIDConversion {

    public static void use(MapperConfiguration config) {
        config.addConverter(
            UUID.class,
            String.class,
            source -> context -> source == null ? null : source.toString()
        );
    }
}
