package test.easymapper;

import java.util.UUID;

import autoparams.AutoSource;
import autoparams.Repeat;
import easymapper.Mapper;
import easymapper.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({ "ConstantValue", "DataFlowIssue" })
public class SpecsForConvert {

    @AllArgsConstructor
    @Getter
    public static class User {

        private final long id;
        private final String username;
        private final String passwordHash;
    }

    @Test
    @AutoDomainParams
    void convert_has_null_guard_for_sourceType(Mapper sut, User source) {
        Class<User> sourceType = null;
        assertThatThrownBy(() -> sut.convert(source, sourceType, User.class))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("sourceType");
    }

    @Test
    @AutoDomainParams
    void convert_has_null_guard_for_targetType(Mapper sut, User source) {
        Class<User> targetType = null;
        assertThatThrownBy(() -> sut.convert(source, User.class, targetType))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("targetType");
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_object(Mapper sut, User source) {
        User actual = sut.convert(source, User.class, User.class);

        assertThat(actual).isNotSameAs(source);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }

    @Test
    @AutoDomainParams
    void convert_converts_null_value_to_null_value(Mapper sut) {
        User actual = sut.convert(null, User.class, User.class);
        assertThat(actual).isNull();
    }

    @Test
    @AutoDomainParams
    void convert_ignores_extra_properties(Mapper sut, User source) {
        UserView actual = sut.convert(source, UserView.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }

    @Test
    @AutoDomainParams
    void convert_without_source_type_has_null_guard_for_source(Mapper sut) {
        User source = null;
        assertThatThrownBy(() -> sut.convert(source, User.class))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("source");
    }

    @Test
    @AutoDomainParams
    void convert_without_source_type_has_null_guard_for_targetType(
        Mapper sut,
        User source
    ) {
        Class<User> targetType = null;
        assertThatThrownBy(() -> sut.convert(source, targetType))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("targetType");
    }

    @Test
    @AutoDomainParams
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

    @Test
    @AutoDomainParams
    void convert_works_with_default_constructor(Mapper sut, UserView source) {
        UserView actual = sut.convert(source, UserView.class);

        assertThat(actual).isNotNull();
        assertThat(actual).isNotSameAs(source);
    }

    @Test
    @AutoDomainParams
    void convert_correctly_sets_setter_properties(Mapper sut, UserView source) {
        UserView actual = sut.convert(source, UserView.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
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

    @Test
    @AutoDomainParams
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

    @ParameterizedTest
    @AutoSource
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
    public static class ImmutableBag<T> {

        private final T value;
    }

    @Test
    @AutoDomainParams
    void convert_with_type_references_has_null_guard_for_sourceTypeReference(
        Mapper sut,
        ImmutableBag<UUID> source
    ) {
        TypeReference<ImmutableBag<UUID>> sourceTypeReference = null;

        ThrowableAssert.ThrowingCallable action = () -> sut.convert(
            source,
            sourceTypeReference,
            new TypeReference<ImmutableBag<String>>() { }
        );

        assertThatThrownBy(action).isInstanceOf(NullPointerException.class);
    }

    @Test
    @AutoDomainParams
    void convert_with_type_references_has_null_guard_for_targetTypeReference(
        Mapper sut,
        ImmutableBag<UUID> source
    ) {
        TypeReference<ImmutableBag<String>> targetTypeReference = null;

        ThrowableAssert.ThrowingCallable action = () -> sut.convert(
            source,
            new TypeReference<ImmutableBag<UUID>>() { },
            targetTypeReference
        );

        assertThatThrownBy(action).isInstanceOf(NullPointerException.class);
    }

    @Test
    @AutoDomainParams
    void convert_with_type_references_correctly_converts_value_of_type_argument_for_constructors(
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

    @Test
    @AutoDomainParams
    void convert_with_type_references_correctly_converts_value_of_type_argument_for_setters(
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

    @Test
    @AutoDomainParams
    void convert_with_type_references_correctly_converts_value_of_deep_type_argument_for_constructors(
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

    @Test
    @AutoDomainParams
    void convert_with_type_references_correctly_converts_value_of_deep_type_argument_for_setters(
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

    @Getter
    public static class UserDto {

        private final long id;
        private final String name;

        public UserDto(long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Test
    @AutoDomainParams
    void convert_fails_with_useful_message_if_cannot_resolve_parameter_names(
        Mapper sut,
        UserView source
    ) {
        assertThatThrownBy(() -> sut.convert(source, UserDto.class))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContainingAll(
                "UserDto",
                "@ConstructorProperties"
            );
    }
}
