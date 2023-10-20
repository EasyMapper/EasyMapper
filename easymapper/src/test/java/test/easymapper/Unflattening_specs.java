package test.easymapper;

import easymapper.Mapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static org.assertj.core.api.Assertions.assertThat;

public class Unflattening_specs {

    @Getter
    @Setter
    public static class ShipmentEntity {
        private long id;
        private String recipientName;
        private String recipientPhoneNumber;
        private String addressCountry;
        private String addressState;
        private String addressCity;
        private String addressZipCode;
    }

    @AllArgsConstructor
    @Getter
    public static class Shipment {
        private final long id;
        private final Recipient recipient;
        private final Address address;
    }

    @AllArgsConstructor
    @Getter
    public static class Recipient {
        private final String name;
        private final String phoneNumber;
    }
    @AllArgsConstructor
    @Getter
    public static class Address {
        private final String country;
        private final String state;
        private final String city;
        private final String zipCode;
    }

    @Getter
    @Setter
    public static class ShipmentView {
        private long id;
        private Recipient recipient;
        private Address address;
    }

    @Getter
    @Setter
    public static class ReceiptEntity {
        private long priceAmount;
        private String priceCurrency;
        private long shipmentId;
        private String shipmentRecipientName;
        private String shipmentRecipientPhoneNumber;
        private String shipmentAddressCountry;
        private String shipmentAddressState;
        private String shipmentAddressCity;
        private String shipmentAddressZipCode;
    }

    @AllArgsConstructor
    @Getter
    public static class Price {
        private final long amount;
        private final String currency;
    }

    @AllArgsConstructor
    @Getter
    public static class Receipt {
        private final Price price;
        private final Shipment shipment;
    }

    @AutoParameterizedTest
    void sut_correctly_unflattens_properties_for_constructors(
        Mapper sut,
        ShipmentEntity source
    ) {
        // Act
        Shipment destination = sut.map(
            source,
            ShipmentEntity.class,
            Shipment.class);

        // Assert
        assertThat(destination.getRecipient().getName())
            .isEqualTo(source.getRecipientName());
        assertThat(destination.getRecipient().getPhoneNumber())
            .isEqualTo(source.getRecipientPhoneNumber());
        assertThat(destination.getAddress().getCountry())
            .isEqualTo(source.getAddressCountry());
        assertThat(destination.getAddress().getState())
            .isEqualTo(source.getAddressState());
        assertThat(destination.getAddress().getCity())
            .isEqualTo(source.getAddressCity());
        assertThat(destination.getAddress().getZipCode())
            .isEqualTo(source.getAddressZipCode());
    }

    @AutoParameterizedTest
    void sut_correctly_unflattens_properties_for_setters(
        Mapper sut,
        ShipmentEntity source
    ) {
        // Act
        ShipmentView destination = sut.map(
            source,
            ShipmentEntity.class,
            ShipmentView.class);

        // Assert
        assertThat(destination.getRecipient().getName())
            .isEqualTo(source.getRecipientName());
        assertThat(destination.getRecipient().getPhoneNumber())
            .isEqualTo(source.getRecipientPhoneNumber());
        assertThat(destination.getAddress().getCountry())
            .isEqualTo(source.getAddressCountry());
        assertThat(destination.getAddress().getState())
            .isEqualTo(source.getAddressState());
        assertThat(destination.getAddress().getCity())
            .isEqualTo(source.getAddressCity());
        assertThat(destination.getAddress().getZipCode())
            .isEqualTo(source.getAddressZipCode());
    }

    @AutoParameterizedTest
    void sut_correctly_unflattens_deep_properties_for_constructors(
        Mapper sut,
        ReceiptEntity source
    ) {
        // Act
        Receipt destination = sut.map(
            source,
            ReceiptEntity.class,
            Receipt.class);

        // Assert
        assertThat(destination.getPrice().getAmount())
            .isEqualTo(source.getPriceAmount());
        assertThat(destination.getPrice().getCurrency())
            .isEqualTo(source.getPriceCurrency());
        assertThat(destination.getShipment().getId())
            .isEqualTo(source.getShipmentId());
        assertThat(destination.getShipment().getRecipient().getName())
            .isEqualTo(source.getShipmentRecipientName());
        assertThat(destination.getShipment().getRecipient().getPhoneNumber())
            .isEqualTo(source.getShipmentRecipientPhoneNumber());
        assertThat(destination.getShipment().getAddress().getCountry())
            .isEqualTo(source.getShipmentAddressCountry());
        assertThat(destination.getShipment().getAddress().getState())
            .isEqualTo(source.getShipmentAddressState());
        assertThat(destination.getShipment().getAddress().getCity())
            .isEqualTo(source.getShipmentAddressCity());
        assertThat(destination.getShipment().getAddress().getZipCode())
            .isEqualTo(source.getShipmentAddressZipCode());
    }
}
