package test.easymapper;

import autoparams.Repeat;
import easymapper.Mapper;
import easymapper.TypeReference;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({ "DataFlowIssue", "ConstantValue" })
class Mapper_specs {

    @Test
    void constructor_has_guard_against_null_configurer() {
        assertThatThrownBy(() -> new Mapper(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AllArgsConstructor
    @Getter
    public static class User {
        private final long id;
        private final String username;
        private final String passwordHash;
    }

    @Getter
    @Setter
    public static class UserView {
        private long id;
        private String username;
    }

    @AutoParameterizedTest
    void converting_map_has_null_guard_for_source_type(
        Mapper sut,
        User source
    ) {
        Class<User> sourceType = null;
        assertThatThrownBy(
            () -> sut.map(source, sourceType, User.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("sourceType");
    }

    @AutoParameterizedTest
    void converting_map_has_null_guard_for_destination_type(
        Mapper sut,
        User source
    ) {
        Class<User> destinationType = null;
        assertThatThrownBy(
            () -> sut.map(source, User.class, destinationType))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("destinationType");
    }

    @AutoParameterizedTest
    void converting_map_correctly_converts_object(
        Mapper sut,
        User source
    ) {
        User actual = sut.map(source, User.class, User.class);

        assertThat(actual).isNotSameAs(source);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }

    @AutoParameterizedTest
    void converting_map_works_with_default_constructor(
        Mapper sut,
        UserView source
    ) {
        UserView actual = sut.map(source, UserView.class, UserView.class);

        assertThat(actual).isNotNull();
        assertThat(actual).isNotSameAs(source);
    }

    @AutoParameterizedTest
    void converting_map_correctly_sets_setter_properties(
        Mapper sut,
        UserView source
    ) {
        UserView actual = sut.map(source, UserView.class, UserView.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }

    @AutoParameterizedTest
    void converting_map_ignores_extra_properties(
        Mapper sut,
        User source
    ) {
        UserView actual = sut.map(source, User.class, UserView.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }

    @AutoParameterizedTest
    void converting_map_converts_null_value_to_null_value(
        Mapper sut
    ) {
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
    void converting_map_creates_copy_of_complex_object_for_constructor_properties(
        Mapper sut,
        Order source
    ) {
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

    @AllArgsConstructor
    @Getter
    public static class DiscountPolicy {
        private final boolean enabled;
        private final int percentage;
    }

    @AutoParameterizedTest
    @Repeat(10)
    void converting_map_accepts_is_prefix_for_getter_of_boolean_properties(
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
    void converting_map_converts_uuid_to_string(
        Mapper sut,
        Post source
    ) {
        PostView actual = sut.map(source, Post.class, PostView.class);

        assertThat(actual.getId()).isEqualTo(source.getId().toString());
        assertThat(actual.getAuthorId()).isEqualTo(source.getAuthorId().toString());
    }

    @AutoParameterizedTest
    void converting_map_converts_null_uuid_to_null_string(
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
    void converting_map_correctly_converts_big_integer_value(
        Mapper sut,
        BigInteger source
    ) {
        BigInteger actual = sut.map(source, BigInteger.class, BigInteger.class);
        assertThat(actual).isEqualTo(source);
    }

    @AutoParameterizedTest
    void converting_map_correctly_converts_big_decimal_value(
        Mapper sut,
        BigDecimal source
    ) {
        BigDecimal actual = sut.map(source, BigDecimal.class, BigDecimal.class);
        assertThat(actual).isEqualTo(source);
    }

    @AutoParameterizedTest
    void converting_map_correctly_converts_local_date_value(
        Mapper sut,
        LocalDate source
    ) {
        LocalDate actual = sut.map(source, LocalDate.class, LocalDate.class);
        assertThat(actual).isEqualTo(source);
    }

    @AutoParameterizedTest
    void converting_map_correctly_converts_local_time_value(
        Mapper sut,
        LocalTime source
    ) {
        LocalTime actual = sut.map(source, LocalTime.class, LocalTime.class);
        assertThat(actual).isEqualTo(source);
    }

    @AutoParameterizedTest
    void converting_map_correctly_converts_local_date_time_value(
        Mapper sut,
        LocalDateTime source
    ) {
        LocalDateTime actual = sut.map(
            source,
            LocalDateTime.class,
            LocalDateTime.class);

        assertThat(actual).isEqualTo(source);
    }

    @AllArgsConstructor
    @Getter
    public static class ImmutableBag<T> {
        private final T value;
    }

    @Getter
    @Setter
    public static class MutableBag<T> {
        private T value;
    }

    @AutoParameterizedTest
    void converting_map_with_type_reference_has_null_guard_for_destination_type_reference(
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
    void converting_map_with_type_reference_has_null_guard_for_source_type_reference(
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
    void converting_map_with_type_reference_correctly_converts_value_of_type_argument_for_constructors(
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

    @AutoParameterizedTest
    void converting_map_with_type_reference_correctly_converts_value_of_type_argument_for_setters(
        Mapper sut,
        ImmutableBag<UUID> source
    ) {
        MutableBag<String> actual = sut.map(
            source,
            new TypeReference<ImmutableBag<UUID>>() {},
            new TypeReference<MutableBag<String>>() {});

        assertThat(actual).isNotNull();
        assertThat(actual.getValue()).isEqualTo(source.getValue().toString());
    }

    @AutoParameterizedTest
    void converting_map_with_type_reference_correctly_converts_value_of_deep_type_argument_for_constructors(
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
    void converting_map_with_type_reference_correctly_converts_value_of_deep_type_argument_for_setters(
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

    @AutoParameterizedTest
    void projecting_map_has_null_guard_for_source(
        Mapper sut,
        UserView destination
    ) {
        User source = null;
        assertThatThrownBy(
            () -> sut.map(source, destination, User.class, UserView.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("source");
    }

    @AutoParameterizedTest
    void projecting_map_has_null_guard_for_destination(
        Mapper sut,
        User source
    ) {
        UserView destination = null;
        assertThatThrownBy(
            () -> sut.map(source, destination, User.class, UserView.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("destination");
    }

    @AutoParameterizedTest
    void projecting_map_has_null_guard_for_source_type(
        Mapper sut,
        User source,
        UserView destination
    ) {
        Class<User> sourceType = null;
        assertThatThrownBy(
            () -> sut.map(source, destination, sourceType, UserView.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("sourceType");
    }

    @AutoParameterizedTest
    void projecting_map_has_null_guard_for_destination_type(
        Mapper sut,
        User source,
        UserView destination
    ) {
        Class<UserView> destinationType = null;
        assertThatThrownBy(
            () -> sut.map(source, destination, User.class, destinationType))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("destinationType");
    }

    @AutoParameterizedTest
    void projection_map_correctly_projects_properties(
        Mapper sut,
        Post source,
        PostView destination
    ) {
        sut.map(source, destination, Post.class, PostView.class);

        assertThat(destination.getId()).isEqualTo(source.getId().toString());
        assertThat(destination.getAuthorId()).isEqualTo(source.getAuthorId().toString());
        assertThat(destination.getTitle()).isEqualTo(source.getTitle());
        assertThat(destination.getText()).isEqualTo(source.getText());
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
    void projecting_map_ignores_for_missing_property_of_target(
        Mapper sut,
        Pricing source,
        PricingView destination
    ) {
        double snapshot = destination.getSalePrice();
        sut.map(source, destination, Pricing.class, PricingView.class);
        assertThat(destination.getSalePrice()).isEqualTo(snapshot);
    }

    @AutoParameterizedTest
    void projecting_map_ignores_extra_properties_of_source(
        Mapper sut,
        User source
    ) {
        UserView destination = new UserView();
        sut.map(source, destination, User.class, UserView.class);
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
    void projecting_map_creates_copy_of_complex_object_for_setter_properties(
        Mapper sut,
        Order source,
        OrderView destination
    ) {
        // Act
        sut.map(source, destination, Order.class, OrderView.class);

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
    void projecting_map_fails_if_source_property_is_null_and_read_only_destination_property_is_not_null(
        Mapper sut,
        User destination
    ) {
        User source = new User(
            destination.getId(),
            destination.getUsername(),
            null);

        assertThatThrownBy(() -> sut.map(
            source,
            destination,
            User.class,
            User.class))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("passwordHash");
    }

    @AutoParameterizedTest
    void projecting_map_fails_if_source_property_is_not_null_and_read_only_destination_property_is_null(
        Mapper sut,
        User source
    ) {
        User destination = new User(source.getId(), source.getUsername(), null);

        assertThatThrownBy(() -> sut.map(
            source,
            destination,
            User.class,
            User.class))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("passwordHash");
    }

    @AutoParameterizedTest
    void projecting_map_with_type_reference_has_null_guard_for_source(
        Mapper sut,
        MutableBag<String> destination
    ) {
        assertThatThrownBy(() -> sut.map(
            null,
            destination,
            new TypeReference<MutableBag<UUID>>() {},
            new TypeReference<MutableBag<String>>() {}))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void projecting_map_with_type_reference_has_null_guard_for_destination(
        Mapper sut,
        MutableBag<UUID> source
    ) {
        assertThatThrownBy(() -> sut.map(
            source,
            null,
            new TypeReference<MutableBag<UUID>>() {},
            new TypeReference<MutableBag<String>>() {}))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void projecting_map_with_type_reference_has_null_guard_for_source_type_reference(
        Mapper sut,
        MutableBag<UUID> source,
        MutableBag<String> destination
    ) {
        TypeReference<MutableBag<UUID>> sourceTypeReference = null;
        assertThatThrownBy(() -> sut.map(
            source,
            destination,
            sourceTypeReference,
            new TypeReference<MutableBag<String>>() {}))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void projecting_map_with_type_reference_has_null_guard_for_destination_type_reference(
        Mapper sut,
        MutableBag<UUID> source,
        MutableBag<String> destination
    ) {
        TypeReference<MutableBag<String>> destinationTypeReference = null;
        assertThatThrownBy(() -> sut.map(
            source,
            destination,
            new TypeReference<MutableBag<UUID>>() {},
            destinationTypeReference))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void projecting_map_with_type_reference_correctly_converts_value_of_type_argument(
        Mapper sut,
        MutableBag<UUID> source,
        MutableBag<String> destination
    ) {
        sut.map(
            source,
            destination,
            new TypeReference<MutableBag<UUID>>() {},
            new TypeReference<MutableBag<String>>() {});

        assertThat(destination.getValue())
            .isEqualTo(source.getValue().toString());
    }

    @AutoParameterizedTest
    void projecting_map_with_type_reference_correctly_maps_value_of_deep_type_argument(
        Mapper sut,
        MutableBag<MutableBag<UUID>> source,
        MutableBag<MutableBag<String>> destination
    ) {
        sut.map(
            source,
            destination,
            new TypeReference<MutableBag<MutableBag<UUID>>>() {},
            new TypeReference<MutableBag<MutableBag<String>>>() {});

        assertThat(destination.getValue().getValue())
            .isEqualTo(source.getValue().getValue().toString());
    }

    @AutoParameterizedTest
    void projecting_map_with_no_type_hint_has_null_guard_for_source(
        Mapper sut,
        User destination
    ) {
        assertThatThrownBy(() -> sut.map(null, destination))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("source");
    }

    @AutoParameterizedTest
    void projecting_map_with_no_type_hint_has_null_guard_for_destination(
        Mapper sut,
        User source
    ) {
        assertThatThrownBy(() -> sut.map(source, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("destination");
    }

    @AutoParameterizedTest
    void projecting_map_with_no_type_hint_correctly_maps_properties(
        Mapper sut,
        User source,
        UserView destination
    ) {
        sut.map(source, destination);

        assertThat(destination.getId()).isEqualTo(source.getId());
        assertThat(destination.getUsername()).isEqualTo(source.getUsername());
    }
}
