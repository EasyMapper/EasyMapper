package test.easymapper;

import autoparams.Repeat;
import easymapper.Mapper;
import easymapper.TypeReference;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class Conversion_specs {

    @AllArgsConstructor
    @Getter
    public static class User {
        private final long id;
        private final String username;
        private final String passwordHash;
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
    void map_correctly_converts_object(User source) {
        Mapper sut = new Mapper();
        User actual = sut.map(source, User.class, User.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }

    @AutoParameterizedTest
    void map_works_with_default_constructor(Mapper sut, User source) {
        UserView actual = sut.map(source, User.class, UserView.class);
        assertThat(actual).isNotNull();
    }

    @AutoParameterizedTest
    void map_ignores_extra_properties(Mapper sut, User source) {
        UserView actual = sut.map(source, User.class, UserView.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }

    @AutoParameterizedTest
    void map_converts_null_value_to_null_value(Mapper sut) {
        User actual = sut.map(null, User.class, User.class);
        assertThat(actual).isNull();
    }

    @AllArgsConstructor
    @Getter
    public static class Order {
        private final UUID id;
        private final long itemId;
        private final int quantity;
        private final Shipment shipment;
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

    @AllArgsConstructor
    @Getter
    public static class Address {
        private final String country;
        private final String state;
        private final String city;
        private final String zipCode;
    }

    @AutoParameterizedTest
    void map_creates_copy_of_complex_object(Mapper sut, Order source) {
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

    @Getter
    @Setter
    public static class UserView {
        private long id;
        private String username;
    }


    @AutoParameterizedTest
    void map_correctly_sets_settable_properties(
        Mapper sut,
        UserView source
    ) {
        UserView destination = sut.map(
            source,
            UserView.class,
            UserView.class);

        assertThat(destination)
            .usingRecursiveComparison()
            .isEqualTo(source);
    }

    @AllArgsConstructor
    @Getter
    public static class DiscountPolicy {
        private final boolean enabled;
        private final int percentage;
    }

    @AutoParameterizedTest
    @Repeat(10)
    void map_accepts_is_prefix_for_getter_of_boolean_properties(
        Mapper sut,
        DiscountPolicy source
    ) {
        DiscountPolicy actual = sut.map(
            source,
            DiscountPolicy.class,
            DiscountPolicy.class);

        assertThat(actual.isEnabled()).isEqualTo(source.isEnabled());
    }

    @AllArgsConstructor
    @Getter
    public static class Post {
        private final UUID id;
        private final UUID authorId;
        private final String title;
        private final String text;
    }

    @Getter
    @Setter
    public static class PostView {
        private String id;
        private String authorId;
        private String title;
        private String text;
    }

    @AutoParameterizedTest
    void map_converts_uuid_to_string(
        Mapper sut,
        Post source
    ) {
        PostView actual = sut.map(source, Post.class, PostView.class);

        assertThat(actual.getId()).isEqualTo(source.getId().toString());
        assertThat(actual.getAuthorId()).isEqualTo(source.getAuthorId().toString());
    }

    @AutoParameterizedTest
    void map_converts_null_uuid_to_null_string(
        Mapper sut,
        UUID authorId,
        String title,
        String text
    ) {
        Post source = new Post(null, authorId, title, text);
        PostView actual = sut.map(source, Post.class, PostView.class);

        assertThat(actual.getId()).isNull();
    }

    @AutoParameterizedTest
    void map_correctly_converts_big_integer_value(
        Mapper sut,
        BigInteger source
    ) {
        BigInteger actual = sut.map(source, BigInteger.class, BigInteger.class);
        assertThat(actual).isEqualTo(source);
    }

    @AutoParameterizedTest
    void map_correctly_converts_big_decimal_value(
        Mapper sut,
        BigDecimal source
    ) {
        BigDecimal actual = sut.map(source, BigDecimal.class, BigDecimal.class);
        assertThat(actual).isEqualTo(source);
    }

    @AutoParameterizedTest
    void map_correctly_converts_local_date_value(
        Mapper sut,
        LocalDate source
    ) {
        LocalDate actual = sut.map(source, LocalDate.class, LocalDate.class);
        assertThat(actual).isEqualTo(source);
    }

    @AutoParameterizedTest
    void map_correctly_converts_local_time_value(
        Mapper sut,
        LocalTime source
    ) {
        LocalTime actual = sut.map(source, LocalTime.class, LocalTime.class);
        assertThat(actual).isEqualTo(source);
    }

    @AutoParameterizedTest
    void map_correctly_converts_local_date_time_value(
        Mapper sut,
        LocalDateTime source
    ) {
        LocalDateTime actual = sut.map(
            source,
            LocalDateTime.class,
            LocalDateTime.class);

        assertThat(actual).isEqualTo(source);
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
    void map_with_types_correctly_converts_object(Mapper sut, User source) {
        Type type = User.class;
        User actual = sut.map(source, type, type);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }

    @AllArgsConstructor
    @Getter
    public static class ImmutableBag<T> {
        private final T value;
    }

    @AutoParameterizedTest
    void map_has_null_guard_for_destination_type_reference(
        Mapper sut,
        ImmutableBag<UUID> source
    ) {
        TypeReference<ImmutableBag<String>> destinationTypeReference = null;
        assertThatThrownBy(() -> sut.map(
            source,
            new TypeReference<ImmutableBag<UUID>>() {},
            destinationTypeReference))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void map_has_null_guard_for_source_type_reference(
        Mapper sut,
        ImmutableBag<UUID> source
    ) {
        TypeReference<ImmutableBag<UUID>> sourceTypeReference = null;
        assertThatThrownBy(() -> sut.map(
            source,
            sourceTypeReference,
            new TypeReference<ImmutableBag<String>>() {}))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void map_with_type_reference_correctly_converts_value_of_type_argument_for_constructors(
        Mapper sut,
        ImmutableBag<UUID> source
    ) {
        ImmutableBag<String> actual = sut.map(
            source,
            new TypeReference<ImmutableBag<UUID>>() {},
            new TypeReference<ImmutableBag<String>>() {});

        assertThat(actual).isNotNull();
        assertThat(actual.getValue()).isEqualTo(source.getValue().toString());
    }

    @Getter
    @Setter
    public static class MutableBag<T> {
        private T value;
    }

    @AutoParameterizedTest
    void map_with_type_reference_correctly_converts_value_of_type_argument_for_setters(
        Mapper sut,
        MutableBag<UUID> source
    ) {
        MutableBag<String> actual = sut.map(
            source,
            new TypeReference<MutableBag<UUID>>() {},
            new TypeReference<MutableBag<String>>() {});

        assertThat(actual).isNotNull();
        assertThat(actual.getValue()).isEqualTo(source.getValue().toString());
    }

    @AutoParameterizedTest
    void map_with_type_reference_correctly_converts_value_of_deep_type_argument_for_constructors(
        Mapper sut,
        ImmutableBag<ImmutableBag<UUID>> source
    ) {
        ImmutableBag<ImmutableBag<String>> actual = sut.map(
            source,
            new TypeReference<ImmutableBag<ImmutableBag<UUID>>>() {},
            new TypeReference<ImmutableBag<ImmutableBag<String>>>() {});

        assertThat(actual).isNotNull();
        assertThat(actual.getValue().getValue())
            .isEqualTo(source.getValue().getValue().toString());
    }

    @AutoParameterizedTest
    void map_with_type_reference_correctly_converts_value_of_deep_type_argument_for_setters(
        Mapper sut,
        MutableBag<MutableBag<UUID>> source
    ) {
        MutableBag<MutableBag<String>> actual = sut.map(
            source,
            new TypeReference<MutableBag<MutableBag<UUID>>>() {},
            new TypeReference<MutableBag<MutableBag<String>>>() {});

        assertThat(actual).isNotNull();
        assertThat(actual.getValue().getValue())
            .isEqualTo(source.getValue().getValue().toString());
    }
}
