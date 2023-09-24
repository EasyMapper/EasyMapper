package test.easymapper;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Price {
    private final String currency;
    private final BigDecimal amount;
}
