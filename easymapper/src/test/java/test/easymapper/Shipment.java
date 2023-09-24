package test.easymapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Shipment {
    private final Recipient recipient;
    private final Address address;
}
