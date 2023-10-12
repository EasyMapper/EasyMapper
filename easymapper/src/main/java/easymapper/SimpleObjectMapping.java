package easymapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static java.util.function.Function.identity;

class SimpleObjectMapping {

    public static void configure(MapperConfiguration config) {
        config.map(UUID.class, UUID.class,
            mapping -> mapping.convert(context -> identity()));

        config.map(UUID.class, String.class,
            mapping -> mapping
                .convert(context -> SimpleObjectMapping::asString));

        config.map(String.class, String.class,
            mapping -> mapping
                .convert(context -> identity())
                .project((source, target) -> context -> {}));

        config.map(BigInteger.class, BigInteger.class,
            mapping -> mapping.convert(context -> identity()));

        config.map(BigDecimal.class, BigDecimal.class,
            mapping -> mapping.convert(context -> identity()));

        config.map(LocalDate.class, LocalDate.class,
            mapping -> mapping.convert(context -> identity()));

        config.map(LocalTime.class, LocalTime.class,
            mapping -> mapping.convert(context -> identity()));


        config.map(LocalDateTime.class, LocalDateTime.class,
            mapping -> mapping.convert(context -> identity()));
    }

    private static <T> String asString(T source) {
        return source == null ? null : source.toString();
    }
}
