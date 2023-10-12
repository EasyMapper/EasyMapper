package test.easymapper;

import easymapper.Mapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static org.assertj.core.api.Assertions.assertThat;

public class Flattening_specs {

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
    public static class Shipment {
        private final Recipient recipient;
        private final Address address;
    }

    @AllArgsConstructor
    @Getter
    public static class Recipient {
        private final String name;
        private final String phoneNumber;
    }

    @Getter
    @Setter
    public static class ShipmentEntity {
        private String recipientName;
        private String recipientPhoneNumber;
        private String addressCountry;
        private String addressState;
        private String addressCity;
        private String addressZipCode;
    }

    @AllArgsConstructor
    @Getter
    public static class ShipmentView {
        private final String recipientName;
        private final String recipientPhoneNumber;
        private final Address address;
    }

    @AutoParameterizedTest
    void sut_correctly_flattens_nested_properties_for_constructors(
        Mapper sut,
        Shipment source
    ) {
        // Act
        ShipmentView actual = sut.map(
            source,
            Shipment.class,
            ShipmentView.class);

        // Assert
        assertThat(actual.getRecipientName())
            .isEqualTo(source.getRecipient().getName());
        assertThat(actual.getRecipientPhoneNumber())
            .isEqualTo(source.getRecipient().getPhoneNumber());
    }

    @AutoParameterizedTest
    void sut_correctly_flattens_nested_properties_for_setters(
        Mapper sut,
        Shipment source
    ) {
        // Act
        ShipmentEntity actual = sut.map(
            source,
            Shipment.class,
            ShipmentEntity.class);

        // Assert
        assertThat(actual.getRecipientName())
            .isEqualTo(source.getRecipient().getName());
        assertThat(actual.getRecipientPhoneNumber())
            .isEqualTo(source.getRecipient().getPhoneNumber());
    }

    @AutoParameterizedTest
    void sut_correctly_flattens_nested_properties_for_setters_with_null(
        Mapper sut,
        Address address
    ) {
        Recipient recipient = null;
        Shipment source = new Shipment(recipient, address);

        ShipmentEntity actual = sut.map(
            source,
            Shipment.class,
            ShipmentEntity.class);

        assertThat(actual.getRecipientName()).isNull();
        assertThat(actual.getRecipientPhoneNumber()).isNull();
    }
}
