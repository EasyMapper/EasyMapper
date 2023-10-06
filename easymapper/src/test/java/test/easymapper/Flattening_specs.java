package test.easymapper;

import easymapper.Mapper;
import test.easymapper.fixture.Address;
import test.easymapper.fixture.Recipient;
import test.easymapper.fixture.Shipment;
import test.easymapper.fixture.ShipmentEntity;
import test.easymapper.fixture.ShipmentView;

import static org.assertj.core.api.Assertions.assertThat;

public class Flattening_specs {

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
