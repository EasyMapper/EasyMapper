package test.easymapper.fixture;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShipmentEntity {
    private String recipientName;
    private String recipientPhoneNumber;
    private String addressCountry;
    private String addressState;
    private String addressCity;
    private String addressZipCode;
}
