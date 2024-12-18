package test.easymapper;

import easymapper.Mapper;
import easymapper.MappingBuilder;
import easymapper.Projection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

import static java.lang.String.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("DataFlowIssue")
public class MappingBuilder_specs {

    @AllArgsConstructor
    @Getter
    public static class User {

        private final int id;
        private final String username;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class UserView {

        private String id;
        private String name;

        public static UserView from(User user) {
            return new UserView(valueOf(user.getId()), user.getUsername());
        }
    }

    @Test
    void convert_has_null_guard_for_function() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.map(
                User.class,
                UserView.class,
                mapping -> mapping.convert(null)
            )
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("function");
    }

    @Test
    void convert_is_fluent() {
        new Mapper(
            config -> config.map(
                User.class,
                UserView.class,
                mapping -> {
                    MappingBuilder<User, UserView> actual = mapping.convert(
                        (context, source) -> UserView.from(source)
                    );
                    assertThat(actual).isSameAs(mapping);
                }
            )
        );
    }

    @AutoParameterizedTest
    void convert_correctly_works(User user) {
        Mapper mapper = new Mapper(
            config -> config.map(
                User.class,
                UserView.class,
                mapping -> mapping.convert(
                    (context, source) -> UserView.from(source)
                )
            )
        );

        UserView actual = mapper.convert(user, UserView.class);

        assertThat(actual.getId()).isEqualTo(valueOf(user.getId()));
        assertThat(actual.getName()).isEqualTo(user.getUsername());
    }

    @Test
    void convert_throws_exception_if_conversion_already_set() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.map(
                User.class,
                UserView.class,
                mapping -> mapping
                    .convert((context, source) -> UserView.from(source))
                    .convert((context, source) -> UserView.from(source))
            )
        );

        assertThatThrownBy(action).isInstanceOf(RuntimeException.class);
    }

    @Test
    void project_has_null_guard_for_action() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.map(
                User.class,
                UserView.class,
                mapping -> mapping.project(null)
            )
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("action");
    }

    @Test
    void project_is_fluent() {
        new Mapper(
            config -> config.map(
                User.class,
                UserView.class,
                mapping -> {
                    MappingBuilder<User, UserView> actual = mapping.project(
                        Projection.empty()
                    );
                    assertThat(actual).isSameAs(mapping);
                }
            )
        );
    }


    @AutoParameterizedTest
    void project_correctly_works(User user, UserView view) {
        Mapper mapper = new Mapper(
            config -> config.map(
                User.class,
                UserView.class,
                mapping -> mapping.project(
                    (context, source, destination) -> {
                        destination.setId(valueOf(source.getId()));
                        destination.setName(source.getUsername());
                    }
                )
            )
        );

        mapper.project(user, view);

        assertThat(view.getId()).isEqualTo(valueOf(user.getId()));
        assertThat(view.getName()).isEqualTo(user.getUsername());
    }

    @AllArgsConstructor
    @Getter
    public static class UserBag {
        private final User value;
    }

    @AllArgsConstructor
    @Getter
    public static class UserViewBag {
        private final UserView value;
    }

    @AutoParameterizedTest
    void project_projects_to_existing_value_of_read_only_destination_properties(
        UserBag userBag,
        UserViewBag userViewBag
    ) {
        // Arrange
        Mapper mapper = new Mapper(
            config -> config.map(
                User.class,
                UserView.class,
                mapping -> mapping.project(
                    (context, source, destination) -> {
                        destination.setId(valueOf(source.getId()));
                        destination.setName(source.getUsername());
                    }
                )
            )
        );

        // Act
        mapper.project(userBag, userViewBag);

        // Assert
        UserView actual = userViewBag.getValue();
        assertThat(actual.getId())
            .isEqualTo(valueOf(userBag.getValue().getId()));
        assertThat(actual.getName())
            .isEqualTo(userBag.getValue().getUsername());
    }


    @Test
    void project_throws_exception_if_projection_already_set() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.map(
                User.class,
                UserView.class,
                mapping -> mapping
                    .project(Projection.empty())
                    .project(Projection.empty())
            )
        );

        assertThatThrownBy(action).isInstanceOf(RuntimeException.class);
    }


    @Test
    void compute_has_null_guard_for_destination_property_name() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.map(
                User.class,
                UserView.class,
                mapping -> mapping.compute(
                    null,
                    (context, source) -> valueOf(source.getId())
                )
            )
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("destinationPropertyName");
    }

    @Test
    void compute_has_null_guard_for_function() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.map(
                User.class,
                UserView.class,
                mapping -> mapping.compute("id", null)
            )
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("function");
    }

    @Test
    void compute_is_fluent() {
        new Mapper(
            config -> config.map(
                User.class,
                UserView.class,
                mapping -> {
                    MappingBuilder<User, UserView> actual = mapping.compute(
                        "id",
                        (context, source) -> valueOf(source.getId())
                    );
                    assertThat(actual).isSameAs(mapping);
                }
            )
        );
    }

    @AutoParameterizedTest
    void compute_correctly_works_for_constructor_properties(User user) {
        Mapper mapper = new Mapper(
            config -> config.map(
                User.class,
                UserView.class,
                mapping -> mapping
                    .compute("id", (context, source) -> valueOf(source.getId()))
                    .compute("name", (context, source) -> source.getUsername())
            )
        );

        UserView actual = mapper.convert(user, UserView.class);

        assertThat(actual.getId()).isEqualTo(valueOf(user.getId()));
        assertThat(actual.getName()).isEqualTo(user.getUsername());
    }

    @AutoParameterizedTest
    void compute_correctly_works_for_constructor_properties_even_if_computed_value_is_null(
        User user
    ) {
        Mapper mapper = new Mapper(
            config -> config.map(
                User.class,
                UserView.class,
                mapping -> mapping
                    .compute("id", (context, source) -> valueOf(source.getId()))
                    .compute("name", (context, source) -> null)
            )
        );

        UserView actual = mapper.convert(user, UserView.class);

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
        Mapper mapper = new Mapper(
            config -> config.map(
                Pricing.class,
                PricingView.class,
                mapping -> mapping.compute(
                    "salePrice",
                    (context, source) -> source.calculateSalePrice()
                )
            )
        );

        PricingView actual = mapper.convert(pricing, PricingView.class);

        assertThat(actual.getListPrice()).isEqualTo(pricing.getListPrice());
        assertThat(actual.getDiscount()).isEqualTo(pricing.getDiscount());
        assertThat(actual.getSalePrice()).isEqualTo(pricing.calculateSalePrice());
    }

    @Test
    void compute_does_not_allow_duplicate_destination_property_name() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.map(
                User.class,
                UserView.class,
                mapping -> mapping
                    .compute("id", (context, source) -> valueOf(source.getId()))
                    .compute("id", (context, source) -> null)
            )
        );

        assertThatThrownBy(action);
    }
}
