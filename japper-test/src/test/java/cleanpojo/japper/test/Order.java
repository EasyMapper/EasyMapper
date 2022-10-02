package cleanpojo.japper.test;

import java.beans.ConstructorProperties;
import java.util.UUID;

public final class Order {
    private UUID id;
    private long itemId;
    private int amount;
    private Address shippingAddress;

    @ConstructorProperties({ "id", "itemId", "amount", "shippingAddress" })
    public Order(UUID id, long itemId, int amount, Address shippingAddress) {
        this.id = id;
        this.itemId = itemId;
        this.amount = amount;
        this.shippingAddress = shippingAddress;
    }

    public UUID getId() {
        return id;
    }

    public long getItemId() {
        return itemId;
    }

    public int getAmount() {
        return amount;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }
}
