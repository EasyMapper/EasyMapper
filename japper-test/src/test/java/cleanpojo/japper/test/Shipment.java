package cleanpojo.japper.test;

import java.beans.ConstructorProperties;

public final class Shipment {

    private Recipient recipient;
    private Address address;

    @ConstructorProperties({ "recipient", "address" })
    public Shipment(Recipient recipient, Address address) {
        this.recipient = recipient;
        this.address = address;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public Address getAddress() {
        return address;
    }
}
