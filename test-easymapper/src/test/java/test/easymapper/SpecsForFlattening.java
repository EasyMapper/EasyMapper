package test.easymapper;

import javax.persistence.Entity;
import javax.persistence.Id;

import easymapper.Mapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SpecsForFlattening {

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

    @AllArgsConstructor
    @Getter
    public static class ShipmentView {

        private final String recipientName;
        private final String recipientPhoneNumber;
        private final Address address;
    }

    @Test
    @AutoDomainParams
    void convert_correctly_flattens_nested_properties_for_constructors(
        Mapper sut,
        Shipment source
    ) {
        ShipmentView actual = sut.convert(source, ShipmentView.class);

        assertThat(actual.getRecipientName())
            .isEqualTo(source.getRecipient().getName());
        assertThat(actual.getRecipientPhoneNumber())
            .isEqualTo(source.getRecipient().getPhoneNumber());
    }

    @Entity
    @Getter
    @Setter
    public static class ShipmentEntity {

        @Id
        private Long id;

        private String recipientName;

        private String recipientPhoneNumber;

        private String addressCountry;

        private String addressState;

        private String addressCity;

        private String addressZipCode;
    }

    @Test
    @AutoDomainParams
    void convert_correctly_flattens_nested_properties_for_setters(
        Mapper sut,
        Shipment source
    ) {
        ShipmentEntity actual = sut.convert(source, ShipmentEntity.class);

        assertThat(actual.getRecipientName())
            .isEqualTo(source.getRecipient().getName());
        assertThat(actual.getRecipientPhoneNumber())
            .isEqualTo(source.getRecipient().getPhoneNumber());
    }

    @Test
    @AutoDomainParams
    @UseNull(Recipient.class)
    void convert_correctly_flattens_nested_properties_for_setters_with_null(
        Mapper sut,
        Shipment source
    ) {
        ShipmentEntity actual = sut.convert(source, ShipmentEntity.class);

        assertThat(actual.getRecipientName()).isNull();
        assertThat(actual.getRecipientPhoneNumber()).isNull();
    }

    @Test
    @AutoDomainParams
    void convert_correctly_unflattens_properties_for_constructors(
        Mapper sut,
        ShipmentEntity source
    ) {
        Shipment target = sut.convert(source, Shipment.class);

        assertThat(target.getRecipient().getName())
            .isEqualTo(source.getRecipientName());
        assertThat(target.getRecipient().getPhoneNumber())
            .isEqualTo(source.getRecipientPhoneNumber());
        assertThat(target.getAddress().getCountry())
            .isEqualTo(source.getAddressCountry());
        assertThat(target.getAddress().getState())
            .isEqualTo(source.getAddressState());
        assertThat(target.getAddress().getCity())
            .isEqualTo(source.getAddressCity());
        assertThat(target.getAddress().getZipCode())
            .isEqualTo(source.getAddressZipCode());
    }

    @Getter
    @Setter
    public static class ShipmentDto {

        private long id;
        private Recipient recipient;
        private Address address;
    }

    @Test
    @AutoDomainParams
    void convert_correctly_unflattens_properties_for_setters(
        Mapper sut,
        ShipmentEntity source
    ) {
        ShipmentDto target = sut.convert(source, ShipmentDto.class);

        assertThat(target.getRecipient().getName())
            .isEqualTo(source.getRecipientName());
        assertThat(target.getRecipient().getPhoneNumber())
            .isEqualTo(source.getRecipientPhoneNumber());
        assertThat(target.getAddress().getCountry())
            .isEqualTo(source.getAddressCountry());
        assertThat(target.getAddress().getState())
            .isEqualTo(source.getAddressState());
        assertThat(target.getAddress().getCity())
            .isEqualTo(source.getAddressCity());
        assertThat(target.getAddress().getZipCode())
            .isEqualTo(source.getAddressZipCode());
    }

    @AllArgsConstructor
    @Getter
    public static class Receipt {

        private final Price price;
        private final Shipment shipment;
    }

    @AllArgsConstructor
    @Getter
    public static class Price {

        private final long amount;
        private final String currency;
    }

    @Entity
    @Getter
    @Setter
    public static class ReceiptEntity {

        @Id
        private Long id;

        private Long priceAmount;

        private String priceCurrency;

        private Long shipmentId;

        private String shipmentRecipientName;

        private String shipmentRecipientPhoneNumber;

        private String shipmentAddressCountry;

        private String shipmentAddressState;

        private String shipmentAddressCity;

        private String shipmentAddressZipCode;
    }

    @Test
    @AutoDomainParams
    void convert_correctly_unflattens_deep_properties_for_constructors(
        Mapper sut,
        ReceiptEntity source
    ) {
        Receipt target = sut.convert(source, Receipt.class);

        assertThat(target.getPrice().getAmount())
            .isEqualTo(source.getPriceAmount());
        assertThat(target.getPrice().getCurrency())
            .isEqualTo(source.getPriceCurrency());
        assertThat(target.getShipment().getId())
            .isEqualTo(source.getShipmentId());
        assertThat(target.getShipment().getRecipient().getName())
            .isEqualTo(source.getShipmentRecipientName());
        assertThat(target.getShipment().getRecipient().getPhoneNumber())
            .isEqualTo(source.getShipmentRecipientPhoneNumber());
        assertThat(target.getShipment().getAddress().getCountry())
            .isEqualTo(source.getShipmentAddressCountry());
        assertThat(target.getShipment().getAddress().getState())
            .isEqualTo(source.getShipmentAddressState());
        assertThat(target.getShipment().getAddress().getCity())
            .isEqualTo(source.getShipmentAddressCity());
        assertThat(target.getShipment().getAddress().getZipCode())
            .isEqualTo(source.getShipmentAddressZipCode());
    }
}
