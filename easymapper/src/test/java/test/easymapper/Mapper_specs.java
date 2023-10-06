package test.easymapper;

import autoparams.Repeat;
import easymapper.Mapper;
import org.junit.jupiter.api.Test;
import test.easymapper.fixture.DiscountPolicy;
import test.easymapper.fixture.Order;
import test.easymapper.fixture.User;
import test.easymapper.fixture.UserEntity;
import test.easymapper.fixture.UserView;

import java.lang.reflect.Type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Mapper_specs {

    @Test
    void constructor_has_guard_against_null_configurer() {
        assertThatThrownBy(() -> new Mapper(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void map_has_null_guard_for_source_type(Mapper sut, User source) {
        Class<User> sourceType = null;
        assertThatThrownBy(
            () -> sut.map(source, sourceType, User.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("sourceType");
    }

    @AutoParameterizedTest
    void map_has_null_guard_for_destination_type(Mapper sut, User source) {
        Class<User> destinationType = null;
        assertThatThrownBy(
            () -> sut.map(source, User.class, destinationType))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("destinationType");
    }

    @AutoParameterizedTest
    void projecting_map_has_null_guard_for_source(
        Mapper sut,
        User destination
    ) {
        User source = null;
        assertThatThrownBy(
            () -> sut.map(source, destination, User.class, User.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("source");
    }

    @AutoParameterizedTest
    void projecting_map_has_null_guard_for_destination(
        Mapper sut,
        User source
    ) {
        User destination = null;
        assertThatThrownBy(
            () -> sut.map(source, destination, User.class, User.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("destination");
    }

    @AutoParameterizedTest
    void projecting_map_has_null_guard_for_source_type(
        Mapper sut,
        User source,
        User destination
    ) {
        Class<User> sourceType = null;
        assertThatThrownBy(
            () -> sut.map(source, destination, sourceType, User.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("sourceType");
    }

    @AutoParameterizedTest
    void projecting_map_has_null_guard_for_destination_type(
        Mapper sut,
        User source,
        User destination
    ) {
        Class<User> destinationType = null;
        assertThatThrownBy(
            () -> sut.map(source, destination, User.class, destinationType))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("destinationType");
    }

    @AutoParameterizedTest
    void map_with_types_has_null_guard_for_source(Mapper sut) {
        Type type = User.class;
        assertThatThrownBy(() -> sut.map(null, type, type))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("source");
    }

    @AutoParameterizedTest
    void map_with_types_has_null_guard_for_source_type(
        Mapper sut,
        User source
    ) {
        Type sourceType = null;
        assertThatThrownBy(() -> sut.map(source, sourceType, User.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("sourceType");
    }

    @AutoParameterizedTest
    void map_with_types_has_null_guard_for_destination_type(
        Mapper sut,
        User source
    ) {
        Type destinationType = null;
        assertThatThrownBy(() -> sut.map(source, User.class, destinationType))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("destinationType");
    }

    @AutoParameterizedTest
    void sut_correctly_maps_object(User source) {
        Mapper sut = new Mapper();
        User actual = sut.map(source, User.class, User.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }

    @AutoParameterizedTest
    void sut_works_with_default_constructor(Mapper sut, User source) {
        UserView actual = sut.map(source, User.class, UserView.class);
        assertThat(actual).isNotNull();
    }

    @AutoParameterizedTest
    void sut_ignores_extra_properties(Mapper sut, User source) {
        UserView actual = sut.map(source, User.class, UserView.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }

    @AutoParameterizedTest
    void sut_maps_null_value_to_null_value(Mapper sut) {
        User actual = sut.map(null, User.class, User.class);
        assertThat(actual).isNull();
    }

    @AutoParameterizedTest
    void sut_creates_copy_of_complex_object(Mapper sut, Order source) {
        // Act
        Order actual = sut.map(source, Order.class, Order.class);

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
        UserEntity destination = sut.map(
            source,
            UserEntity.class,
            UserEntity.class);

        assertThat(destination)
            .usingRecursiveComparison()
            .ignoringFields("passwordHash")
            .isEqualTo(source);
    }

    @AutoParameterizedTest
    @Repeat(10)
    void sut_accepts_is_prefix_for_getter_of_boolean_properties(
        Mapper sut,
        DiscountPolicy source
    ) {
        DiscountPolicy actual = sut.map(
            source,
            DiscountPolicy.class,
            DiscountPolicy.class);

        assertThat(actual.isEnabled()).isEqualTo(source.isEnabled());
    }

    @AutoParameterizedTest
    void map_with_types_correctly_maps_object(Mapper sut, User source) {
        Type type = User.class;
        User actual = sut.map(source, type, type);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }
}
