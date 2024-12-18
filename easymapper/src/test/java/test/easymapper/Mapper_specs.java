package test.easymapper;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.beans.ConstructorProperties;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import autoparams.Repeat;
import easymapper.Mapper;
import easymapper.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({ "DataFlowIssue", "ConstantValue" })
class Mapper_specs {

    @Test
    void constructor_has_guard_against_null_configurer() {
        assertThatThrownBy(() -> new Mapper(null))
            .isInstanceOf(NullPointerException.class);
    }

    @AllArgsConstructor
    @Getter
    public static class User {

        private final long id;
        private final String username;
        private final String passwordHash;
    }

    @AutoParameterizedTest
    void convert_has_null_guard_for_source_type(Mapper sut, User source) {
        Class<User> sourceType = null;
        assertThatThrownBy(
            () -> sut.convert(source, sourceType, User.class))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("sourceType");
    }

    @AutoParameterizedTest
    void convert_has_null_guard_for_destination_type(Mapper sut, User source) {
        Class<User> destinationType = null;
        assertThatThrownBy(
            () -> sut.convert(source, User.class, destinationType))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("destinationType");
    }

    @AutoParameterizedTest
    void convert_correctly_converts_object(Mapper sut, User source) {
        User actual = sut.convert(source, User.class, User.class);

        assertThat(actual).isNotSameAs(source);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }

    @AutoParameterizedTest
    void convert_without_source_type_has_null_guard_for_destination_type(
        Mapper sut,
        User source
    ) {
        Class<User> destinationType = null;
        assertThatThrownBy(
            () -> sut.convert(source, destinationType))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("destinationType");
    }

    @AutoParameterizedTest
    void convert_without_source_type_correctly_converts_object(
        Mapper sut,
        User source
    ) {
        User actual = sut.convert(source, User.class);

        assertThat(actual).isNotSameAs(source);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class UserView {

        private long id;
        private String username;
    }

    @AutoParameterizedTest
    void convert_works_with_default_constructor(Mapper sut, UserView source) {
        UserView actual = sut.convert(source, UserView.class);

        assertThat(actual).isNotNull();
        assertThat(actual).isNotSameAs(source);
    }

    @AutoParameterizedTest
    void convert_correctly_sets_setter_properties(Mapper sut, UserView source) {
        UserView actual = sut.convert(source, UserView.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }

    @AutoParameterizedTest
    void convert_ignores_extra_properties(Mapper sut, User source) {
        UserView actual = sut.convert(source, UserView.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }

    @AutoParameterizedTest
    void convert_converts_null_value_to_null_value(Mapper sut) {
        User actual = sut.convert(null, User.class, User.class);
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

        private final Address address;
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
    void convert_creates_copy_of_complex_object_for_constructor_properties(
        Mapper sut,
        Order source
    ) {
        // Act
        Order actual = sut.convert(source, Order.class);

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

    @AllArgsConstructor
    @Getter
    public static class DiscountPolicy {

        private final boolean enabled;
        private final int percentage;
    }

    @AutoParameterizedTest
    @Repeat(10)
    void convert_accepts_is_prefix_for_getter_of_boolean_properties(
        Mapper sut,
        DiscountPolicy source
    ) {
        DiscountPolicy actual = sut.convert(source, DiscountPolicy.class);
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
    void convert_converts_uuid_to_string(Mapper sut, Post source) {
        PostView actual = sut.convert(source, PostView.class);

        assertThat(actual.getId()).isEqualTo(source.getId().toString());
        assertThat(actual.getAuthorId()).isEqualTo(source.getAuthorId().toString());
    }

    @AutoParameterizedTest
    void convert_converts_null_uuid_to_null_string(
        Mapper sut,
        UUID authorId,
        String title,
        String text
    ) {
        Post source = new Post(null, authorId, title, text);
        PostView actual = sut.convert(source, PostView.class);

        assertThat(actual.getId()).isNull();
    }

    @AutoParameterizedTest
    void convert_correctly_converts_big_integer_value(
        Mapper sut,
        BigInteger source
    ) {
        BigInteger actual = sut.convert(source, BigInteger.class);
        assertThat(actual).isEqualTo(source);
    }

    @AutoParameterizedTest
    void convert_correctly_converts_big_decimal_value(
        Mapper sut,
        BigDecimal source
    ) {
        BigDecimal actual = sut.convert(source, BigDecimal.class);
        assertThat(actual).isEqualTo(source);
    }

    @AutoParameterizedTest
    void convert_correctly_converts_local_date_value(
        Mapper sut,
        LocalDate source
    ) {
        LocalDate actual = sut.convert(source, LocalDate.class);
        assertThat(actual).isEqualTo(source);
    }

    @AutoParameterizedTest
    void convert_correctly_converts_local_time_value(
        Mapper sut,
        LocalTime source
    ) {
        LocalTime actual = sut.convert(source, LocalTime.class);
        assertThat(actual).isEqualTo(source);
    }

    @AutoParameterizedTest
    void convert_correctly_converts_local_date_time_value(
        Mapper sut,
        LocalDateTime source
    ) {
        LocalDateTime actual = sut.convert(source, LocalDateTime.class);
        assertThat(actual).isEqualTo(source);
    }

    @AllArgsConstructor
    @Getter
    public static class ImmutableBag<T> {

        private final T value;
    }

    @AutoParameterizedTest
    void convert_with_type_reference_has_null_guard_for_source_type_reference(
        Mapper sut,
        ImmutableBag<UUID> source
    ) {
        TypeReference<ImmutableBag<UUID>> sourceTypeReference = null;

        ThrowingCallable action = () -> sut.convert(
            source,
            sourceTypeReference,
            new TypeReference<ImmutableBag<String>>() { }
        );

        assertThatThrownBy(action).isInstanceOf(NullPointerException.class);
    }

    @AutoParameterizedTest
    void convert_with_type_reference_has_null_guard_for_destination_type_reference(
        Mapper sut,
        ImmutableBag<UUID> source
    ) {
        TypeReference<ImmutableBag<String>> destinationTypeReference = null;

        ThrowingCallable action = () -> sut.convert(
            source,
            new TypeReference<ImmutableBag<UUID>>() { },
            destinationTypeReference
        );

        assertThatThrownBy(action).isInstanceOf(NullPointerException.class);
    }

    @AutoParameterizedTest
    void convert_with_type_reference_correctly_converts_value_of_type_argument_for_constructors(
        Mapper sut,
        ImmutableBag<UUID> source
    ) {
        ImmutableBag<String> actual = sut.convert(
            source,
            new TypeReference<ImmutableBag<UUID>>() { },
            new TypeReference<ImmutableBag<String>>() { }
        );

        assertThat(actual).isNotNull();
        assertThat(actual.getValue()).isEqualTo(source.getValue().toString());
    }

    @Getter
    @Setter
    public static class MutableBag<T> {

        private T value;
    }

    @AutoParameterizedTest
    void convert_with_type_reference_correctly_converts_value_of_type_argument_for_setters(
        Mapper sut,
        ImmutableBag<UUID> source
    ) {
        MutableBag<String> actual = sut.convert(
            source,
            new TypeReference<ImmutableBag<UUID>>() { },
            new TypeReference<MutableBag<String>>() { }
        );

        assertThat(actual).isNotNull();
        assertThat(actual.getValue()).isEqualTo(source.getValue().toString());
    }

    @AutoParameterizedTest
    void convert_with_type_reference_correctly_converts_value_of_deep_type_argument_for_constructors(
        Mapper sut,
        ImmutableBag<ImmutableBag<UUID>> source
    ) {
        ImmutableBag<ImmutableBag<String>> actual = sut.convert(
            source,
            new TypeReference<ImmutableBag<ImmutableBag<UUID>>>() { },
            new TypeReference<ImmutableBag<ImmutableBag<String>>>() { }
        );

        assertThat(actual).isNotNull();
        assertThat(actual.getValue().getValue())
            .isEqualTo(source.getValue().getValue().toString());
    }

    @AutoParameterizedTest
    void convert_with_type_reference_correctly_converts_value_of_deep_type_argument_for_setters(
        Mapper sut,
        MutableBag<MutableBag<UUID>> source
    ) {
        MutableBag<MutableBag<String>> actual = sut.convert(
            source,
            new TypeReference<MutableBag<MutableBag<UUID>>>() { },
            new TypeReference<MutableBag<MutableBag<String>>>() { }
        );

        assertThat(actual).isNotNull();
        assertThat(actual.getValue().getValue())
            .isEqualTo(source.getValue().getValue().toString());
    }

    @AutoParameterizedTest
    void project_has_null_guard_for_source(
        Mapper sut,
        UserView destination
    ) {
        User source = null;

        ThrowingCallable action = () -> sut.project(
            source,
            destination,
            User.class,
            UserView.class
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("source");
    }

    @AutoParameterizedTest
    void project_has_null_guard_for_destination(
        Mapper sut,
        User source
    ) {
        UserView destination = null;

        ThrowingCallable action = () -> sut.project(
            source,
            destination,
            User.class,
            UserView.class
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("destination");
    }

    @AutoParameterizedTest
    void project_has_null_guard_for_source_type(
        Mapper sut,
        User source,
        UserView destination
    ) {
        Class<User> sourceType = null;

        ThrowingCallable action = () -> sut.project(
            source,
            destination,
            sourceType,
            UserView.class
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("sourceType");
    }

    @AutoParameterizedTest
    void project_has_null_guard_for_destination_type(
        Mapper sut,
        User source,
        UserView destination
    ) {
        Class<UserView> destinationType = null;

        ThrowingCallable action = () -> sut.project(
            source,
            destination,
            User.class,
            destinationType
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("destinationType");
    }

    @AutoParameterizedTest
    void project_correctly_projects_properties(
        Mapper sut,
        Post source,
        PostView destination
    ) {
        sut.project(source, destination, Post.class, PostView.class);

        assertThat(destination.getId()).isEqualTo(source.getId().toString());
        assertThat(destination.getAuthorId()).isEqualTo(source.getAuthorId().toString());
        assertThat(destination.getTitle()).isEqualTo(source.getTitle());
        assertThat(destination.getText()).isEqualTo(source.getText());
    }

    @AutoParameterizedTest
    void project_with_no_type_hint_has_null_guard_for_source(
        Mapper sut,
        User destination
    ) {
        assertThatThrownBy(() -> sut.project(null, destination))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("source");
    }

    @AutoParameterizedTest
    void project_with_no_type_hint_has_null_guard_for_destination(
        Mapper sut,
        User source
    ) {
        assertThatThrownBy(() -> sut.project(source, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("destination");
    }

    @AutoParameterizedTest
    void project_with_no_type_hint_correctly_maps_properties(
        Mapper sut,
        User source,
        UserView destination
    ) {
        sut.project(source, destination);

        assertThat(destination.getId()).isEqualTo(source.getId());
        assertThat(destination.getUsername()).isEqualTo(source.getUsername());
    }

    @AllArgsConstructor
    @Getter
    public static class Pricing {

        private final double listPrice;
        private final double discountRate;
    }

    @Getter
    @Setter
    public static class PricingView {

        private double listPrice;
        private double discountRate;
        private double salePrice;
    }

    @AutoParameterizedTest
    void project_ignores_for_missing_property_of_target(
        Mapper sut,
        Pricing source,
        PricingView destination
    ) {
        double snapshot = destination.getSalePrice();
        sut.project(source, destination);
        assertThat(destination.getSalePrice()).isEqualTo(snapshot);
    }

    @AutoParameterizedTest
    void project_ignores_extra_properties_of_source(
        Mapper sut,
        User source
    ) {
        UserView destination = new UserView();
        sut.project(source, destination);
        assertThat(destination).usingRecursiveComparison().isEqualTo(source);
    }

    @Getter
    @Setter
    public static class OrderView {

        private UUID id;
        private long itemId;
        private int quantity;
        private Shipment shipment;
    }

    @AutoParameterizedTest
    void project_creates_copy_of_complex_object_for_setter_properties(
        Mapper sut,
        Order source,
        OrderView destination
    ) {
        // Act
        sut.project(source, destination);

        // Assert
        assertThat(destination.getShipment())
            .isNotSameAs(source.getShipment())
            .usingRecursiveComparison()
            .isEqualTo(source.getShipment());

        assertThat(destination.getShipment().getAddress())
            .isNotSameAs(source.getShipment().getAddress())
            .usingRecursiveComparison()
            .isEqualTo(source.getShipment().getAddress());
    }

    @AutoParameterizedTest
    void project_fails_if_source_property_is_null_and_read_only_destination_property_is_not_null(
        Mapper sut,
        User destination
    ) {
        User source = new User(
            destination.getId(),
            destination.getUsername(),
            null
        );

        assertThatThrownBy(() -> sut.project(source, destination))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("passwordHash");
    }

    @AutoParameterizedTest
    void project_fails_if_source_property_is_not_null_and_read_only_destination_property_is_null(
        Mapper sut,
        User source
    ) {
        User destination = new User(source.getId(), source.getUsername(), null);

        assertThatThrownBy(() -> sut.project(source, destination))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("passwordHash");
    }

    @AutoParameterizedTest
    void project_with_type_reference_has_null_guard_for_source(
        Mapper sut,
        MutableBag<String> destination
    ) {
        ThrowingCallable action = () -> sut.project(
            null,
            destination,
            new TypeReference<MutableBag<UUID>>() { },
            new TypeReference<MutableBag<String>>() { }
        );

        assertThatThrownBy(action).isInstanceOf(NullPointerException.class);
    }

    @AutoParameterizedTest
    void project_with_type_reference_has_null_guard_for_destination(
        Mapper sut,
        MutableBag<UUID> source
    ) {
        ThrowingCallable action = () -> sut.project(
            source,
            null,
            new TypeReference<MutableBag<UUID>>() { },
            new TypeReference<MutableBag<String>>() { }
        );

        assertThatThrownBy(action).isInstanceOf(NullPointerException.class);
    }

    @AutoParameterizedTest
    void project_with_type_reference_has_null_guard_for_source_type_reference(
        Mapper sut,
        MutableBag<UUID> source,
        MutableBag<String> destination
    ) {
        TypeReference<MutableBag<UUID>> sourceTypeReference = null;

        ThrowingCallable action = () -> sut.project(
            source,
            destination,
            sourceTypeReference,
            new TypeReference<MutableBag<String>>() { }
        );

        assertThatThrownBy(action).isInstanceOf(NullPointerException.class);
    }

    @AutoParameterizedTest
    void project_with_type_reference_has_null_guard_for_destination_type_reference(
        Mapper sut,
        MutableBag<UUID> source,
        MutableBag<String> destination
    ) {
        TypeReference<MutableBag<String>> destinationTypeReference = null;

        ThrowingCallable action = () -> sut.project(
            source,
            destination,
            new TypeReference<MutableBag<UUID>>() { },
            destinationTypeReference
        );

        assertThatThrownBy(action).isInstanceOf(NullPointerException.class);
    }

    @AutoParameterizedTest
    void project_with_type_reference_correctly_converts_value_of_type_argument(
        Mapper sut,
        MutableBag<UUID> source,
        MutableBag<String> destination
    ) {
        sut.project(
            source,
            destination,
            new TypeReference<MutableBag<UUID>>() { },
            new TypeReference<MutableBag<String>>() { }
        );

        assertThat(destination.getValue())
            .isEqualTo(source.getValue().toString());
    }

    @AutoParameterizedTest
    void project_with_type_reference_correctly_maps_value_of_deep_type_argument(
        Mapper sut,
        MutableBag<MutableBag<UUID>> source,
        MutableBag<MutableBag<String>> destination
    ) {
        sut.project(
            source,
            destination,
            new TypeReference<MutableBag<MutableBag<UUID>>>() { },
            new TypeReference<MutableBag<MutableBag<String>>>() { }
        );

        assertThat(destination.getValue().getValue())
            .isEqualTo(source.getValue().getValue().toString());
    }

    @SuppressWarnings("unused")
    @NoArgsConstructor
    @Getter
    @Entity
    public static class UserEntity {

        @Id
        private long id;

        @Column(unique = true)
        private String username;

        @Column(unique = true)
        @Setter
        private String passwordHash;

        @ConstructorProperties({ "username" })
        public UserEntity(String username) {
            this.username = username;
        }

        @ConstructorProperties({ "id", "username" })
        public UserEntity(long id, String username) {
            this.id = id;
            this.username = username;
        }
    }

    @AutoParameterizedTest
    void convert_chooses_constructor_with_most_parameters(Mapper sut, User source) {
        UserEntity actual = sut.convert(source, UserEntity.class);
        assertThat(actual.getId()).isEqualTo(source.getId());
    }

    @Getter
    public static class UserDto {

        private final long id;
        private final String name;

        public UserDto(long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @AutoParameterizedTest
    void convert_fails_with_useful_message_if_constructor_not_decorated_with_constructor_properties_annotation(
        Mapper sut,
        UserView source
    ) {
        assertThatThrownBy(() -> sut.convert(source, UserDto.class))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContainingAll("UserDto", "@ConstructorProperties");
    }
}
