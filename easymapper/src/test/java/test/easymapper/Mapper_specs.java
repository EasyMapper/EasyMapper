package test.easymapper;

import autoparams.Repeat;
import easymapper.Mapper;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Mapper_specs {

    @AutoParameterizedTest
    void sut_correctly_maps_object(User source) {
        Mapper sut = new Mapper();
        User actual = sut.map(source, User.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }

    @AutoParameterizedTest
    void sut_works_with_default_constructor(Mapper sut, User source) {
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

    @AutoParameterizedTest
    void sut_transforms_uuid_to_string(
        Mapper sut,
        Post source
    ) {
        PostView actual = sut.map(source, PostView.class);

        assertThat(actual.getId()).isEqualTo(source.getId().toString());
        assertThat(actual.getAuthorId()).isEqualTo(source.getAuthorId().toString());
    }

    @AutoParameterizedTest
    void sut_transforms_null_uuid_to_null_string(
        Mapper sut,
        UUID authorId,
        String title,
        String text
    ) {
        Post source = new Post(null, authorId, title, text);
        PostView actual = sut.map(source, PostView.class);

        assertThat(actual.getId()).isNull();
    }

    @AllArgsConstructor
    public static class BooleanBag {
        private final boolean value;

        public boolean getValue() {
            return value;
        }
    }

    @AutoParameterizedTest
    @Repeat(10)
    void sut_correctly_transforms_boolean_value(Mapper sut, BooleanBag source) {
        BooleanBag actual = sut.map(source, BooleanBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class ByteBag {
        private final byte value;
    }

    @AutoParameterizedTest
    void sut_correctly_transforms_byte_value(Mapper sut, ByteBag source) {
        ByteBag actual = sut.map(source, ByteBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class ShortBag {
        private final short value;
    }

    @AutoParameterizedTest
    void sut_correctly_transforms_short_value(Mapper sut, ShortBag source) {
        ShortBag actual = sut.map(source, ShortBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class FloatBag {
        private final float value;
    }

    @AutoParameterizedTest
    void suit_correctly_transforms_float_value(Mapper sut, FloatBag source) {
        FloatBag actual = sut.map(source, FloatBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class DoubleBag {
        private final double value;
    }

    @AutoParameterizedTest
    void sut_correctly_transforms_double_value(Mapper sut, DoubleBag source) {
        DoubleBag actual = sut.map(source, DoubleBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class CharBag {
        private final char value;
    }

    @AutoParameterizedTest
    void sut_correctly_transforms_char_value(Mapper sut, CharBag source) {
        CharBag actual = sut.map(source, CharBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void sut_correctly_transforms_big_integer_value(
        Mapper sut,
        BigInteger source
    ) {
        BigInteger actual = sut.map(source, BigInteger.class);
        assertThat(actual).isEqualTo(source);
    }

    @AutoParameterizedTest
    void sut_correctly_transforms_big_decimal_value(
        Mapper sut,
        BigDecimal source
    ) {
        BigDecimal actual = sut.map(source, BigDecimal.class);
        assertThat(actual).isEqualTo(source);
    }

    @AutoParameterizedTest
    void sut_correctly_transforms_local_date_value(
        Mapper sut,
        LocalDate source
    ) {
        LocalDate actual = sut.map(source, LocalDate.class);
        assertThat(actual).isEqualTo(source);
    }

    @AutoParameterizedTest
    void sut_correctly_transforms_local_time_value(
        Mapper sut,
        LocalTime source
    ) {
        LocalTime actual = sut.map(source, LocalTime.class);
        assertThat(actual).isEqualTo(source);
    }

    @AutoParameterizedTest
    void sut_correctly_transforms_local_date_time_value(
        Mapper sut,
        LocalDateTime source
    ) {
        LocalDateTime actual = sut.map(source, LocalDateTime.class);
        assertThat(actual).isEqualTo(source);
    }
}
