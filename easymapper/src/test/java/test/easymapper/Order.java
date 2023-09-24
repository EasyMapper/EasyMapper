package test.easymapper;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Order {
    private final UUID id;
    private final long itemId;
    private final int quantity;
    private final Shipment shipment;
}
