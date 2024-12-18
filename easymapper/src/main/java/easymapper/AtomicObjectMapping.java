package easymapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

class AtomicObjectMapping {

    public static void configure(MapperConfiguration config) {
        configure(config, String.class);
        configure(config, UUID.class);
        configure(config, BigInteger.class);
        configure(config, BigDecimal.class);
        configure(config, LocalDate.class);
        configure(config, LocalTime.class);
        configure(config, LocalDateTime.class);
    }

    private static <T> void configure(
        MapperConfiguration config,
        Class<T> type
    ) {
        config.map(
            TypePredicate.ACCEPT_ALL_TYPES,
            TypePredicate.from(type),
            mapping -> mapping.project(Projection.empty())
        );

        config.map(type, type, mapping -> mapping
            .convert(Conversion.identity())
            .project(Projection.empty())
        );
    }
}
