package easymapper.test;

import java.beans.ConstructorProperties;
import java.util.UUID;

public class Order {
    private UUID id;
    private long itemId;
    private int quantity;
    private Shipment shipment;

    @ConstructorProperties({ "id", "itemId", "quantity", "shipment" })
    public Order(UUID id, long itemId, int quantity, Shipment shipment) {
        this.id = id;
        this.itemId = itemId;
        this.quantity = quantity;
        this.shipment = shipment;
    }

    public UUID getId() {
        return id;
    }

    public long getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public Shipment getShipment() {
        return shipment;
    }
}
