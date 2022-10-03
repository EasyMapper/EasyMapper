package japper.easymapper;

import java.beans.ConstructorProperties;
import java.util.UUID;

public final class Order {
    private UUID id;
    private long itemId;
    private int amount;
    private Shipment shipment;

    @ConstructorProperties({ "id", "itemId", "amount", "shipment" })
    public Order(UUID id, long itemId, int amount, Shipment shipment) {
        this.id = id;
        this.itemId = itemId;
        this.amount = amount;
        this.shipment = shipment;
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

    public Shipment getShipment() {
        return shipment;
    }
}
