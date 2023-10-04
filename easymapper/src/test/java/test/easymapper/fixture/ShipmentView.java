package test.easymapper.fixture;

import lombok.AllArgsConstructor;
import lombok.Getter;
import test.easymapper.fixture.Address;

@AllArgsConstructor
@Getter
public class ShipmentView {
    private final String recipientName;
    private final String recipientPhoneNumber;
    private final Address address;
}
