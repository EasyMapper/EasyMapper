package test.easymapper.fixture;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PricingView {
    private final double listPrice;
    private final double discount;
    private final double salePrice;
}
