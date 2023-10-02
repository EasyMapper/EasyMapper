package test.easymapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ShipmentView {
    private final String recipientName;
    private final String recipientPhoneNumber;
    private final Address address;
}
