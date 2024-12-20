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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import autoparams.Repeat;
import easymapper.Mapper;
import easymapper.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
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
        assertThatThrownBy(() -> sut.convert(source, sourceType, User.class))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("sourceType");
    }

    @AutoParameterizedTest
    void convert_has_null_guard_for_target_type(Mapper sut, User source) {
        Class<User> targetType = null;

        ThrowingCallable action = () -> sut.convert(
            source,
            User.class,
            targetType
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("targetType");
    }

    @AutoParameterizedTest
    void convert_correctly_converts_object(Mapper sut, User source) {
        User actual = sut.convert(source, User.class, User.class);

        assertThat(actual).isNotSameAs(source);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }

    @AutoParameterizedTest
    void convert_without_source_type_has_null_guard_for_source(Mapper sut) {
        User source = null;
        assertThatThrownBy(() -> sut.convert(source, User.class))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("source");
    }

    @AutoParameterizedTest
    void convert_without_source_type_has_null_guard_for_target_type(
        Mapper sut,
        User source
    ) {
        Class<User> targetType = null;
        assertThatThrownBy(() -> sut.convert(source, targetType))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("targetType");
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
        val source = new Post(null, authorId, title, text);
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
    void convert_with_type_reference_has_null_guard_for_target_type_reference(
        Mapper sut,
        ImmutableBag<UUID> source
    ) {
        TypeReference<ImmutableBag<String>> targetTypeReference = null;

        ThrowingCallable action = () -> sut.convert(
            source,
            new TypeReference<ImmutableBag<UUID>>() { },
            targetTypeReference
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
    void project_has_null_guard_for_source(Mapper sut, UserView target) {
        User source = null;

        ThrowingCallable action = () -> sut.project(
            source,
            target,
            User.class,
            UserView.class
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("source");
    }

    @AutoParameterizedTest
    void project_has_null_guard_for_target(
        Mapper sut,
        User source
    ) {
        UserView target = null;

        ThrowingCallable action = () -> sut.project(
            source,
            target,
            User.class,
            UserView.class
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("target");
    }

    @AutoParameterizedTest
    void project_has_null_guard_for_source_type(
        Mapper sut,
        User source,
        UserView target
    ) {
        Class<User> sourceType = null;

        ThrowingCallable action = () -> sut.project(
            source,
            target,
            sourceType,
            UserView.class
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("sourceType");
    }

    @AutoParameterizedTest
    void project_has_null_guard_for_target_type(
        Mapper sut,
        User source,
        UserView target
    ) {
        Class<UserView> targetType = null;

        ThrowingCallable action = () -> sut.project(
            source,
            target,
            User.class,
            targetType
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("targetType");
    }

    @AutoParameterizedTest
    void project_correctly_projects_properties(
        Mapper sut,
        Post source,
        PostView target
    ) {
        sut.project(source, target, Post.class, PostView.class);

        assertThat(target.getId()).isEqualTo(source.getId().toString());
        assertThat(target.getAuthorId()).isEqualTo(source.getAuthorId().toString());
        assertThat(target.getTitle()).isEqualTo(source.getTitle());
        assertThat(target.getText()).isEqualTo(source.getText());
    }

    @AutoParameterizedTest
    void project_with_no_type_hint_has_null_guard_for_source(
        Mapper sut,
        User target
    ) {
        assertThatThrownBy(() -> sut.project(null, target))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("source");
    }

    @AutoParameterizedTest
    void project_with_no_type_hint_has_null_guard_for_target(
        Mapper sut,
        User source
    ) {
        assertThatThrownBy(() -> sut.project(source, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("target");
    }

    @AutoParameterizedTest
    void project_with_no_type_hint_correctly_maps_properties(
        Mapper sut,
        User source,
        UserView target
    ) {
        sut.project(source, target);

        assertThat(target.getId()).isEqualTo(source.getId());
        assertThat(target.getUsername()).isEqualTo(source.getUsername());
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
        PricingView target
    ) {
        double snapshot = target.getSalePrice();
        sut.project(source, target);
        assertThat(target.getSalePrice()).isEqualTo(snapshot);
    }

    @AutoParameterizedTest
    void project_ignores_extra_properties_of_source(
        Mapper sut,
        User source
    ) {
        val target = new UserView();
        sut.project(source, target);
        assertThat(target).usingRecursiveComparison().isEqualTo(source);
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
        OrderView target
    ) {
        // Act
        sut.project(source, target);

        // Assert
        assertThat(target.getShipment())
            .isNotSameAs(source.getShipment())
            .usingRecursiveComparison()
            .isEqualTo(source.getShipment());

        assertThat(target.getShipment().getAddress())
            .isNotSameAs(source.getShipment().getAddress())
            .usingRecursiveComparison()
            .isEqualTo(source.getShipment().getAddress());
    }

    @AutoParameterizedTest
    void project_does_not_project_if_source_property_is_null_and_read_only_target_property_is_not_null(
        Mapper sut,
        User target
    ) {
        val source = new User(target.getId(), target.getUsername(), null);
        String snapshot = target.getPasswordHash();

        sut.project(source, target);

        assertThat(target.getPasswordHash()).isEqualTo(snapshot);
    }

    @AutoParameterizedTest
    void project_does_not_project_if_source_property_is_not_null_and_read_only_target_property_is_null(
        Mapper sut,
        User source
    ) {
        val target = new User(source.getId(), source.getUsername(), null);
        sut.project(source, target);
        assertThat(target.getPasswordHash()).isNull();
    }

    @AutoParameterizedTest
    void project_with_type_reference_has_null_guard_for_source(
        Mapper sut,
        MutableBag<String> target
    ) {
        ThrowingCallable action = () -> sut.project(
            null,
            target,
            new TypeReference<MutableBag<UUID>>() { },
            new TypeReference<MutableBag<String>>() { }
        );

        assertThatThrownBy(action).isInstanceOf(NullPointerException.class);
    }

    @AutoParameterizedTest
    void project_with_type_reference_has_null_guard_for_target(
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
        MutableBag<String> target
    ) {
        TypeReference<MutableBag<UUID>> sourceTypeReference = null;

        ThrowingCallable action = () -> sut.project(
            source,
            target,
            sourceTypeReference,
            new TypeReference<MutableBag<String>>() { }
        );

        assertThatThrownBy(action).isInstanceOf(NullPointerException.class);
    }

    @AutoParameterizedTest
    void project_with_type_reference_has_null_guard_for_target_type_reference(
        Mapper sut,
        MutableBag<UUID> source,
        MutableBag<String> target
    ) {
        TypeReference<MutableBag<String>> targetTypeReference = null;

        ThrowingCallable action = () -> sut.project(
            source,
            target,
            new TypeReference<MutableBag<UUID>>() { },
            targetTypeReference
        );

        assertThatThrownBy(action).isInstanceOf(NullPointerException.class);
    }

    @AutoParameterizedTest
    void project_with_type_reference_correctly_converts_value_of_type_argument(
        Mapper sut,
        MutableBag<UUID> source,
        MutableBag<String> target
    ) {
        sut.project(
            source,
            target,
            new TypeReference<MutableBag<UUID>>() { },
            new TypeReference<MutableBag<String>>() { }
        );

        assertThat(target.getValue())
            .isEqualTo(source.getValue().toString());
    }

    @AutoParameterizedTest
    void project_with_type_reference_correctly_maps_value_of_deep_type_argument(
        Mapper sut,
        MutableBag<MutableBag<UUID>> source,
        MutableBag<MutableBag<String>> target
    ) {
        sut.project(
            source,
            target,
            new TypeReference<MutableBag<MutableBag<UUID>>>() { },
            new TypeReference<MutableBag<MutableBag<String>>>() { }
        );

        assertThat(target.getValue().getValue())
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

    @AllArgsConstructor
    public static class BooleanBag {

        private final boolean value;

        public boolean getValue() {
            return value;
        }
    }

    @AutoParameterizedTest
    @Repeat(10)
    void convert_correctly_converts_boolean_value(
        Mapper sut,
        BooleanBag source
    ) {
        BooleanBag actual = sut.convert(source, BooleanBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @Getter
    @AllArgsConstructor
    public static class BoxedBooleanBag {

        private final Boolean value;
    }

    @AutoParameterizedTest
    @Repeat(10)
    void convert_correctly_converts_boxed_boolean_value(
        Mapper sut,
        BoxedBooleanBag source
    ) {
        BoxedBooleanBag actual = sut.convert(source, BoxedBooleanBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    @Repeat(10)
    void convert_correctly_converts_boolean_value_to_boxed_boolean_value(
        Mapper sut,
        BooleanBag source
    ) {
        BoxedBooleanBag actual = sut.convert(source, BoxedBooleanBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    @Repeat(10)
    void convert_correctly_converts_boxed_boolean_value_to_boolean_value(
        Mapper sut,
        BoxedBooleanBag source
    ) {
        BooleanBag actual = sut.convert(source, BooleanBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class ByteBag {

        private final byte value;
    }

    @AutoParameterizedTest
    void convert_correctly_converts_byte_value(Mapper sut, ByteBag source) {
        ByteBag actual = sut.convert(source, ByteBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class BoxedByteBag {

        private final Byte value;
    }

    @AutoParameterizedTest
    void convert_correctly_converts_boxed_byte_value(
        Mapper sut,
        BoxedByteBag source
    ) {
        BoxedByteBag actual = sut.convert(source, BoxedByteBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void convert_correctly_converts_byte_value_to_boxed_byte_value(
        Mapper sut,
        ByteBag source
    ) {
        BoxedByteBag actual = sut.convert(source, BoxedByteBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void convert_correctly_converts_boxed_byte_value_to_byte_value(
        Mapper sut,
        BoxedByteBag source
    ) {
        ByteBag actual = sut.convert(source, ByteBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class ShortBag {

        private final short value;
    }

    @AutoParameterizedTest
    void convert_correctly_converts_short_value(Mapper sut, ShortBag source) {
        ShortBag actual = sut.convert(source, ShortBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class BoxedShortBag {

        private final Short value;
    }

    @AutoParameterizedTest
    void convert_correctly_converts_boxed_short_value(
        Mapper sut,
        BoxedShortBag source
    ) {
        BoxedShortBag actual = sut.convert(source, BoxedShortBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void convert_correctly_converts_short_value_to_boxed_short_value(
        Mapper sut,
        ShortBag source
    ) {
        BoxedShortBag actual = sut.convert(source, BoxedShortBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void convert_correctly_converts_boxed_short_value_to_short_value(
        Mapper sut,
        BoxedShortBag source
    ) {
        ShortBag actual = sut.convert(source, ShortBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class IntBag {

        private final int value;
    }

    @AutoParameterizedTest
    void convert_correctly_converts_int_value(Mapper sut, IntBag source) {
        IntBag actual = sut.convert(source, IntBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class BoxedIntBag {

        private final Integer value;
    }

    @AutoParameterizedTest
    void convert_correctly_converts_boxed_int_value(
        Mapper sut,
        BoxedIntBag source
    ) {
        BoxedIntBag actual = sut.convert(source, BoxedIntBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void convert_correctly_converts_int_value_to_boxed_int_value(
        Mapper sut,
        IntBag source
    ) {
        BoxedIntBag actual = sut.convert(source, BoxedIntBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void convert_correctly_converts_boxed_int_value_to_int_value(
        Mapper sut,
        BoxedIntBag source
    ) {
        IntBag actual = sut.convert(source, IntBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class LongBag {

        private final long value;
    }

    @AutoParameterizedTest
    void convert_correctly_converts_long_value(Mapper sut, LongBag source) {
        LongBag actual = sut.convert(source, LongBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class BoxedLongBag {

        private final Long value;
    }

    @AutoParameterizedTest
    void convert_correctly_converts_boxed_long_value(
        Mapper sut,
        BoxedLongBag source
    ) {
        BoxedLongBag actual = sut.convert(source, BoxedLongBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void convert_correctly_converts_long_value_to_boxed_long_value(
        Mapper sut,
        LongBag source
    ) {
        BoxedLongBag actual = sut.convert(source, BoxedLongBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void convert_correctly_converts_boxed_long_value_to_long_value(
        Mapper sut,
        BoxedLongBag source
    ) {
        LongBag actual = sut.convert(source, LongBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class FloatBag {

        private final float value;
    }

    @AutoParameterizedTest
    void convert_correctly_converts_float_value(Mapper sut, FloatBag source) {
        FloatBag actual = sut.convert(source, FloatBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class BoxedFloatBag {

        private final Float value;
    }

    @AutoParameterizedTest
    void convert_correctly_converts_boxed_float_value(
        Mapper sut,
        BoxedFloatBag source
    ) {
        BoxedFloatBag actual = sut.convert(source, BoxedFloatBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void convert_correctly_converts_float_value_to_boxed_float_value(
        Mapper sut,
        FloatBag source
    ) {
        BoxedFloatBag actual = sut.convert(source, BoxedFloatBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void convert_correctly_converts_boxed_float_value_to_float_value(
        Mapper sut,
        BoxedFloatBag source
    ) {
        FloatBag actual = sut.convert(source, FloatBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class DoubleBag {

        private final double value;
    }

    @AutoParameterizedTest
    void convert_correctly_converts_double_value(
        Mapper sut,
        DoubleBag source
    ) {
        DoubleBag actual = sut.convert(source, DoubleBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class BoxedDoubleBag {

        private final Double value;
    }

    @AutoParameterizedTest
    void convert_correctly_converts_boxed_double_value(
        Mapper sut,
        BoxedDoubleBag source
    ) {
        BoxedDoubleBag actual = sut.convert(source, BoxedDoubleBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void convert_correctly_converts_double_value_to_boxed_double_value(
        Mapper sut,
        DoubleBag source
    ) {
        BoxedDoubleBag actual = sut.convert(source, BoxedDoubleBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void convert_correctly_converts_boxed_double_value_to_double_value(
        Mapper sut,
        BoxedDoubleBag source
    ) {
        DoubleBag actual = sut.convert(source, DoubleBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class CharBag {

        private final char value;
    }

    @AutoParameterizedTest
    void convert_correctly_converts_char_value(Mapper sut, CharBag source) {
        CharBag actual = sut.convert(source, CharBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class BoxedCharBag {

        private final Character value;
    }

    @AutoParameterizedTest
    void convert_correctly_converts_boxed_char_value(
        Mapper sut,
        BoxedCharBag source
    ) {
        BoxedCharBag actual = sut.convert(source, BoxedCharBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void convert_correctly_converts_char_value_to_boxed_char_value(
        Mapper sut,
        CharBag source
    ) {
        BoxedCharBag actual = sut.convert(source, BoxedCharBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void convert_correctly_converts_boxed_char_value_to_char_value(
        Mapper sut,
        BoxedCharBag source
    ) {
        CharBag actual = sut.convert(source, CharBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class ShipmentView {

        private final String recipientName;
        private final String recipientPhoneNumber;
        private final Address address;
    }

    @AutoParameterizedTest
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

    @AutoParameterizedTest
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

    @AutoParameterizedTest
    void convert_correctly_flattens_nested_properties_for_setters_with_null(
        Mapper sut,
        long id,
        Address address
    ) {
        Recipient recipient = null;
        val source = new Shipment(id, recipient, address);

        ShipmentEntity actual = sut.convert(source, ShipmentEntity.class);

        assertThat(actual.getRecipientName()).isNull();
        assertThat(actual.getRecipientPhoneNumber()).isNull();
    }

    @AutoParameterizedTest
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

    @AutoParameterizedTest
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

    @AutoParameterizedTest
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

    @Getter
    public static class Employee extends User {

        private final long departmentId;

        @ConstructorProperties({
            "id",
            "username",
            "passwordHash",
            "departmentId"
        })
        public Employee(
            long id,
            String username,
            String passwordHash,
            long departmentId
        ) {
            super(id, username, passwordHash);
            this.departmentId = departmentId;
        }
    }

    @Getter
    @Setter
    public static class EmployeeView extends UserView {

        private long departmentId;
    }

    @AutoParameterizedTest
    void convert_correctly_sets_inherited_properties_through_setters(
        Mapper sut,
        Employee source
    ) {
        EmployeeView actual = sut.convert(source, EmployeeView.class);
        assertThat(actual.getUsername()).isEqualTo(source.getUsername());
    }

    @AutoParameterizedTest
    void convert_correctly_sets_inherited_properties_through_constructors(
        EmployeeView view
    ) {
        val sut = new Mapper(
            config -> config.addExtractor(
                EmployeeView.class,
                Employee.class,
                "passwordHash",
                (context, source) -> null
            )
        );

        Employee actual = sut.convert(view, Employee.class);

        assertThat(actual.getUsername()).isEqualTo(view.getUsername());
    }

    @AllArgsConstructor
    @Getter
    public static class UUIDIterableBag {

        private final Iterable<UUID> value;
    }

    @AllArgsConstructor
    @Getter
    public static class StringIterableBag {

        private final Iterable<String> value;
    }

    @AutoParameterizedTest
    void convert_maps_null_iterable_to_null(Mapper sut) {
        val source = new UUIDIterableBag(null);

        StringIterableBag target = sut.convert(
            source,
            StringIterableBag.class
        );

        assertThat(target.getValue()).isNull();
    }

    @AutoParameterizedTest
    void convert_correctly_maps_iterable_constructor_properties(
        Mapper sut,
        UUIDIterableBag source
    ) {
        StringIterableBag target = sut.convert(
            source,
            StringIterableBag.class
        );

        Iterable<String> actual = target.getValue();
        assertThat(actual).isInstanceOf(ArrayList.class);
        assertThat(actual).isEqualTo(StreamSupport
            .stream(source.getValue().spliterator(), false)
            .map(UUID::toString)
            .collect(Collectors.toList()));
    }

    @AllArgsConstructor
    @Getter
    public static class StringCollectionBag {

        private final Collection<String> value;
    }

    @AutoParameterizedTest
    void sut_correctly_maps_collection_constructor_properties(
        Mapper sut,
        UUIDIterableBag source
    ) {
        StringCollectionBag target = sut.convert(
            source,
            StringCollectionBag.class
        );

        Iterable<String> actual = target.getValue();
        assertThat(actual).isInstanceOf(ArrayList.class);
        assertThat(actual).isEqualTo(StreamSupport
            .stream(source.getValue().spliterator(), false)
            .map(UUID::toString)
            .collect(Collectors.toList()));
    }

    @AllArgsConstructor
    @Getter
    public static class StringListBag {

        private final List<String> value;
    }

    @AutoParameterizedTest
    void convert_correctly_maps_list_constructor_properties(
        Mapper sut,
        UUIDIterableBag source
    ) {
        StringListBag target = sut.convert(source, StringListBag.class);

        Iterable<String> actual = target.getValue();
        assertThat(actual).isInstanceOf(ArrayList.class);
        assertThat(actual).isEqualTo(StreamSupport
            .stream(source.getValue().spliterator(), false)
            .map(UUID::toString)
            .collect(Collectors.toList()));
    }
}
