package test.easymapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Pricing {
    private final double listPrice;
    private final double discount;
}
