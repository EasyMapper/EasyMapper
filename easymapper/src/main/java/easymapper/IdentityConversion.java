package easymapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

class IdentityConversion {

    public static void use(MapperConfiguration config) {
        addIdentityConverter(config, boolean.class);
        addIdentityConverter(config, byte.class);
        addIdentityConverter(config, short.class);
        addIdentityConverter(config, int.class);
        addIdentityConverter(config, long.class);
        addIdentityConverter(config, float.class);
        addIdentityConverter(config, double.class);
        addIdentityConverter(config, char.class);
        addIdentityConverter(config, UUID.class);
        addIdentityConverter(config, String.class);
        addIdentityConverter(config, BigInteger.class);
        addIdentityConverter(config, BigDecimal.class);
        addIdentityConverter(config, LocalDate.class);
        addIdentityConverter(config, LocalTime.class);
        addIdentityConverter(config, LocalDateTime.class);
    }

    private static void addIdentityConverter(
        MapperConfiguration config,
        Class<?> type
    ) {
        config.addConverter(
            type::equals,
            type::equals,
            source -> context -> source);
    }
}
