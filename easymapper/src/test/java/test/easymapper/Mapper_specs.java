package test.easymapper;

import easymapper.Mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Mapper_specs {

    @AutoParameterizedTest
    void sut_correctly_maps_object(Mapper sut, User source) {
        User actual = sut.map(source, User.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }

    @AutoParameterizedTest
    void suit_works_with_default_constructor(Mapper sut, User source) {
        UserView actual = sut.map(source, UserView.class);
        assertThat(actual).isNotNull();
    }

    @AutoParameterizedTest
    void sut_ignores_extra_properties(Mapper sut, User source) {
        UserView actual = sut.map(source, UserView.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }

    @AutoParameterizedTest
    void sut_maps_null_value_to_null_value(Mapper sut) {
        User actual = sut.map(null, User.class);
        assertThat(actual).isNull();
    }

    @AutoParameterizedTest
    void sut_creates_copy_of_complex_object(Mapper sut, Order source) {
        // Arrange

        // Act
        Order actual = sut.map(source, Order.class);

        // Assert
        assertThat(actual.getShipment())
            .isNotSameAs(source.getShipment())
            .usingRecursiveComparison()
            .isEqualTo(source.getShipment());

        assertThat(actual.getShipment().getAddress())
            .isNotSameAs(source.getShipment().getAddress())
            .usingRecursiveComparison()
            .isEqualTo(source.getShipment().getAddress());
    }

    @AutoParameterizedTest
    void sut_correctly_projects_settable_properties(
        Mapper sut,
        UserEntity source
    ) {
        UserEntity destination = sut.map(source, UserEntity.class);

        assertThat(destination)
            .usingRecursiveComparison()
            .ignoringFields("passwordHash")
            .isEqualTo(source);
    }

    @AutoParameterizedTest
    void sut_has_null_guard_for_source(Mapper sut, User destination) {
        assertThatThrownBy(
            () -> sut.map(null, destination, User.class, User.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("source");
    }

    @AutoParameterizedTest
    void sut_has_null_guard_for_destination(Mapper sut, User source) {
        Object destination = null;
        assertThatThrownBy(
            () -> sut.map(source, destination, User.class, User.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("destination");
    }

    @AutoParameterizedTest
    void sut_has_null_guard_for_destination_type(Mapper sut, User source) {
        Class<User> destinationType = null;
        assertThatThrownBy(
            () -> sut.map(source, destinationType))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("destinationType");
    }

    @AutoParameterizedTest
    void sut_chooses_constructor_with_most_parameters(Mapper sut, User source) {
        UserEntity actual = sut.map(source, UserEntity.class);
        assertThat(actual.getId()).isEqualTo(source.getId());
    }

    @AutoParameterizedTest
    void sut_fails_with_useful_message_if_constructor_not_decorated_with_constructor_properties_annotation(
        Mapper sut,
        ItemView source
    ) {
        assertThatThrownBy(() -> sut.map(source, ItemView.class))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContainingAll("ItemView", "@ConstructorProperties");
    }
}
