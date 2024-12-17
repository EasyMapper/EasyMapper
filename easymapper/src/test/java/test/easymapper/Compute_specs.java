package test.easymapper;

import easymapper.Mapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import static java.lang.String.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class Compute_specs {

    @AllArgsConstructor
    @Getter
    public static class User {
        private final int id;
        private final String username;
    }

    @AllArgsConstructor
    @Getter
    public static class UserView {
        private final String id;
        private final String name;
    }

    @Test
    void compute_has_null_guard_for_destination_property_name() {
        assertThatThrownBy(() -> new Mapper(config -> config
            .map(User.class, UserView.class, mapping -> mapping
                .compute(null, (context, source) -> valueOf(source.getId())))))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("destinationPropertyName");
    }

    @Test
    void compute_has_null_guard_for_function() {
        assertThatThrownBy(() -> new Mapper(config -> config
            .map(User.class, UserView.class, mapping -> mapping
                .compute("id", null))))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("function");
    }

    @Test
    void compute_is_fluent() {
        new Mapper(config -> config
            .map(User.class, UserView.class, mapping -> assertThat(mapping
                .compute("id", (context, source) -> valueOf(source.getId())))
                .isSameAs(mapping)));
    }

    @AutoParameterizedTest
    void compute_correctly_works_for_constructor_properties(User user) {
        Mapper mapper = new Mapper(config -> config
            .map(User.class, UserView.class, mapping -> mapping
                .compute("id", (context, source) -> valueOf(source.getId()))
                .compute("name", (context, source) -> source.getUsername())));

        UserView actual = mapper.map(user, User.class, UserView.class);

        assertThat(actual.getId()).isEqualTo(valueOf(user.getId()));
        assertThat(actual.getName()).isEqualTo(user.getUsername());
    }

    @AutoParameterizedTest
    void compute_correctly_works_for_constructor_properties_even_if_computed_value_is_null(
        User user
    ) {
        Mapper mapper = new Mapper(config -> config
            .map(User.class, UserView.class, mapping -> mapping
                .compute("id", (context, source) -> valueOf(source.getId()))
                .compute("name", (context, source) -> null)));

        UserView actual = mapper.map(user, User.class, UserView.class);

        assertThat(actual.getId()).isEqualTo(valueOf(user.getId()));
        assertThat(actual.getName()).isNull();
    }

    @AllArgsConstructor
    @Getter
    public static class Pricing {
        private final double listPrice;
        private final double discount;

        public double calculateSalePrice() {
            return listPrice - discount;
        }
    }

    @Getter
    @Setter
    public static class PricingView {
        private double listPrice;
        private double discount;
        private double salePrice;
    }

    @AutoParameterizedTest
    void compute_correctly_works_for_setter_properties(Pricing pricing) {
        Mapper mapper = new Mapper(config -> config
            .map(Pricing.class, PricingView.class, mapping -> mapping
                .compute("salePrice", (context, source) -> source.calculateSalePrice())));

        PricingView actual = mapper.map(pricing, Pricing.class, PricingView.class);

        assertThat(actual.getListPrice()).isEqualTo(pricing.getListPrice());
        assertThat(actual.getDiscount()).isEqualTo(pricing.getDiscount());
        assertThat(actual.getSalePrice()).isEqualTo(pricing.calculateSalePrice());
    }

    @Test
    void compute_does_not_allow_duplicate_destination_property_name() {
        assertThatThrownBy(() -> new Mapper(config -> config
            .map(User.class, UserView.class, mapping -> mapping
                .compute("id", (context, source) -> valueOf(source.getId()))
                .compute("id", (context, source) -> null))));
    }
}
