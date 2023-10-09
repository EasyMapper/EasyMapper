package easymapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

class SimpleObjectMapping {

    public static void configure(MapperConfiguration config) {
        config.map(UUID.class, UUID.class,
            mapping -> mapping.convert(source -> context -> source));

        config.map(UUID.class, String.class,
            mapping -> mapping.convert(source -> context -> asString(source)));

        config.map(String.class, String.class,
            mapping -> mapping
                .convert(source -> context -> source)
                .project((source, target) -> context -> {}));

        config.map(BigInteger.class, BigInteger.class,
            mapping -> mapping.convert(source -> context -> source));

        config.map(BigDecimal.class, BigDecimal.class,
            mapping -> mapping.convert(source -> context -> source));

        config.map(LocalDate.class, LocalDate.class,
            mapping -> mapping.convert(source -> context -> source));

        config.map(LocalTime.class, LocalTime.class,
            mapping -> mapping.convert(source -> context -> source));


        config.map(LocalDateTime.class, LocalDateTime.class,
            mapping -> mapping.convert(source -> context -> source));
    }

    private static <T> String asString(T source) {
        return source == null ? null : source.toString();
    }
}
