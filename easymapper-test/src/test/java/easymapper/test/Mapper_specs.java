package easymapper.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import autoparams.AutoSource;
import org.junit.jupiter.params.ParameterizedTest;

import easymapper.Mapper;

class Mapper_specs {

    @ParameterizedTest
    @AutoSource
    void sut_correctly_maps_object(Mapper sut, User source) {
        User actual = sut.map(source, User.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }

    @ParameterizedTest
    @AutoSource
    void sut_ignores_extra_properties(Mapper sut, User source) {
        UserView actual = sut.map(source, UserView.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }

    @ParameterizedTest
    @AutoSource
    void sut_maps_null_value_to_null_value(Mapper sut) {
        UserView actual = sut.map(null, UserView.class);
        assertThat(actual).isNull();
    }

    @ParameterizedTest
    @AutoSource
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

    @ParameterizedTest
    @AutoSource
    void sut_correctly_projects_settable_properties(
            Mapper sut,
            UserView source,
            User destination) {

        sut.map(source, destination, UserView.class, User.class);

        assertThat(destination)
                .usingRecursiveComparison()
                .ignoringFields("passwordHash")
                .isEqualTo(source);
    }

    @ParameterizedTest
    @AutoSource
    void sut_has_null_guard_for_source(Mapper sut, User destination) {
        assertThatThrownBy(
                () -> sut.map(null, destination, User.class, User.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("source");
    }

    @ParameterizedTest
    @AutoSource
    void sut_has_null_guard_for_destination(Mapper sut, User source) {
        Object destination = null;
        assertThatThrownBy(
                () -> sut.map(source, destination, User.class, User.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("destination");
    }

    @ParameterizedTest
    @AutoSource
    void sut_has_null_guard_for_destination_type(Mapper sut, User source) {
        Class<User> destinationType = null;
        assertThatThrownBy(() -> sut.map(source, destinationType))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("destinationType");
    }

    @ParameterizedTest
    @AutoSource
    void sut_chooses_constructor_with_most_parameters(Mapper sut, User source) {
        UserEntity actual = sut.map(source, UserEntity.class);
        assertThat(actual.getId()).isEqualTo(source.getId());
    }

    @ParameterizedTest
    @AutoSource
    void sut_fails_with_useful_message_if_constructor_not_decorated_with_constructor_properties_annotation(
            Mapper sut,
            ItemView source) {
        assertThatThrownBy(() -> sut.map(source, ItemView.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContainingAll("ItemView", "@ConstructorProperties");
    }
}
