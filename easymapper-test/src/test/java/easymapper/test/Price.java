package easymapper.test;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;

public class Price {

    private String currency;
    private BigDecimal amount;

    @ConstructorProperties({ "currency", "amount" })
    public Price(String currency, BigDecimal amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
