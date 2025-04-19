package test.easymapper;

import easymapper.Mapper;
import easymapper.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({ "ConstantValue", "DataFlowIssue" })
public class SpecsForProject {

    @AllArgsConstructor
    @Getter
    public static class User {

        private final long id;
        private final String username;
        private final String passwordHash;
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

    @Test
    @AutoDomainParams
    void project_has_null_guard_for_target(Mapper sut, User source) {
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

    @Test
    @AutoDomainParams
    void project_has_null_guard_for_sourceType(
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

    @Test
    @AutoDomainParams
    void project_has_null_guard_for_targetType(
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

    @Test
    @AutoDomainParams
    void project_correctly_projects_properties(
        Mapper sut,
        Post source,
        PostView target
    ) {
        sut.project(source, target, Post.class, PostView.class);

        assertThat(target.getId()).isEqualTo(source.getId().toString());
        assertThat(target.getAuthorId())
            .isEqualTo(source.getAuthorId().toString());
        assertThat(target.getTitle()).isEqualTo(source.getTitle());
        assertThat(target.getText()).isEqualTo(source.getText());
    }

    @Test
    @AutoDomainParams
    void project_with_no_type_hint_has_null_guard_for_source(
        Mapper sut,
        User target
    ) {
        assertThatThrownBy(() -> sut.project(null, target))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("source");
    }

    @Test
    @AutoDomainParams
    void project_with_no_type_hint_has_null_guard_for_target(
        Mapper sut,
        User source
    ) {
        assertThatThrownBy(() -> sut.project(source, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("target");
    }

    @Test
    @AutoDomainParams
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

    @Test
    @AutoDomainParams
    void project_ignores_for_missing_property_of_target(
        Mapper sut,
        Pricing source,
        PricingView target
    ) {
        double snapshot = target.getSalePrice();
        sut.project(source, target);
        assertThat(target.getSalePrice()).isEqualTo(snapshot);
    }

    @Test
    @AutoDomainParams
    void project_ignores_extra_properties_of_source(Mapper sut, User source) {
        val target = new UserView();
        sut.project(source, target);
        assertThat(target).usingRecursiveComparison().isEqualTo(source);
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

    @Getter
    @Setter
    public static class OrderView {

        private UUID id;
        private long itemId;
        private int quantity;
        private Shipment shipment;
    }

    @Test
    @AutoDomainParams
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

    @Test
    @AutoDomainParams
    void project_does_not_project_null_source_property_to_read_only_target_property(
        Mapper sut,
        User target
    ) {
        val source = new User(target.getId(), target.getUsername(), null);
        String snapshot = target.getPasswordHash();

        sut.project(source, target);

        assertThat(target.getPasswordHash()).isEqualTo(snapshot);
    }

    @Test
    @AutoDomainParams
    void project_does_not_project_non_null_source_property_to_read_only_target_property(
        Mapper sut,
        User source
    ) {
        val target = new User(source.getId(), source.getUsername(), null);
        sut.project(source, target);
        assertThat(target.getPasswordHash()).isNull();
    }

    @Getter
    @Setter
    public static class MutableBag<T> {

        private T value;
    }

    @Test
    @AutoDomainParams
    void project_with_type_references_has_null_guard_for_source(
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

    @Test
    @AutoDomainParams
    void project_with_type_references_has_null_guard_for_target(
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

    @Test
    @AutoDomainParams
    void project_with_type_references_has_null_guard_for_sourceTypeReference(
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

    @Test
    @AutoDomainParams
    void project_with_type_references_has_null_guard_for_targetTypeReference(
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

    @Test
    @AutoDomainParams
    void project_with_type_references_correctly_converts_value_of_type_argument(
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

        assertThat(target.getValue()).isEqualTo(source.getValue().toString());
    }

    @Test
    @AutoDomainParams
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
}
