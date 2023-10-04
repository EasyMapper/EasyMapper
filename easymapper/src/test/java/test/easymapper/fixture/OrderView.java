package test.easymapper.fixture;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class OrderView {
    private final UUID id;
    private final long itemId;
    private final int numberOfItems;
    private final Shipment shipment;
}
