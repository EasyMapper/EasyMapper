package test.easymapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Address {
    private final String country;
    private final String state;
    private final String city;
    private final String zipCode;
}
