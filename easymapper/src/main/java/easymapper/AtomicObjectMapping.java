package easymapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

class AtomicObjectMapping {

    public static void configure(MapperConfigurationBuilder config) {
        configure(config, String.class);
        configure(config, UUID.class);
        configure(config, BigInteger.class);
        configure(config, BigDecimal.class);
        configure(config, LocalDate.class);
        configure(config, LocalTime.class);
        configure(config, LocalDateTime.class);
    }

    private static <T> void configure(
        MapperConfigurationBuilder config,
        Class<T> type
    ) {
        config.addProjector(
            TypePredicate.ACCEPT_ALL_TYPES,
            TypePredicate.from(type),
            (source, target, context) -> { }
        );

        config.addConverter(type, type, Converter.identity());
        config.addProjector(type, type, Projector.empty());
    }
}
