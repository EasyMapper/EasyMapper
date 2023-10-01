package test.easymapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DiscountPolicy {
    private final boolean enabled;
    private final int percentage;
}
