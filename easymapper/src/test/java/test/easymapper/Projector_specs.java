package test.easymapper;

import easymapper.Mapper;
import easymapper.MapperConfiguration;
import easymapper.ProjectionContext;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import easymapper.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import test.easymapper.fixture.MutableBag;

import static java.lang.String.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class Projector_specs {

    @AllArgsConstructor
    @Getter
    public static class User {
        private final int id;
        private final String username;
    }

    @Getter
    @Setter
    public static class UserView {
        private String id;
        private String name;
    }

    @Test
    void project_has_null_guard_for_action() {
        assertThatThrownBy(() -> new Mapper(config -> config
            .map(User.class, UserView.class, mapping -> mapping
                .project(null))))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("action");
    }

    @Test
    void project_is_fluent() {
        new Mapper(config -> config
            .map(User.class, UserView.class, mapping -> assertThat(mapping
                .project((source, destination) -> context -> {}))
                .isSameAs(mapping)));
    }

    @AutoParameterizedTest
    void project_correctly_works(User user, UserView view) {
        Mapper mapper = new Mapper(config -> config
            .map(User.class, UserView.class, mapping -> mapping
                .project((source, destination) -> context -> {
                    destination.setId(valueOf(source.getId()));
                    destination.setName(source.getUsername());
                })));

        mapper.map(user, view, User.class, UserView.class);

        assertThat(view.getId()).isEqualTo(valueOf(user.getId()));
        assertThat(view.getName()).isEqualTo(user.getUsername());
    }

    @Test
    void project_throws_exception_if_projection_already_set() {
        ThrowingCallable action = () -> new Mapper(config -> config
            .map(User.class, UserView.class, mapping -> mapping
                .project((source, destination) -> context -> {})
                .project((source, destination) -> context -> {})));

        assertThatThrownBy(action).isInstanceOf(RuntimeException.class);
    }

    @Test
    void addProjector_with_classes_has_null_guard_for_source_type() {
        Class<User> sourceType = null;
        new Mapper(config -> assertThatThrownBy(
            () -> config
                .addProjector(
                    sourceType,
                    UserView.class,
                    (source, destination) -> context -> {}))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("sourceType"));
    }

    @Test
    void addProjector_with_classes_has_null_guard_for_destination_type() {
        Class<UserView> destinationType = null;
        new Mapper(config -> assertThatThrownBy(
            () -> config
                .addProjector(
                    User.class,
                    destinationType,
                    (source, destination) -> context -> {}))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("destinationType"));
    }

    @Test
    void addProjector_with_classes_has_null_guard_for_function() {
        BiFunction<User, UserView, Consumer<ProjectionContext>> consumer = null;
        new Mapper(config -> assertThatThrownBy(
            () -> config
                .addProjector(
                    User.class,
                    UserView.class,
                    consumer))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("consumer"));
    }

    @Test
    void addProjector_with_classes_if_fluent() {
        new Mapper(config -> assertThat(
            config
                .addProjector(
                    User.class,
                    UserView.class,
                    (source, destination) -> context -> {}))
            .isSameAs(config));
    }

    @AutoParameterizedTest
    void addProjector_with_classes_correctly_sets_context(
        User source,
        UserView destination
    ) {
        MutableBag<ProjectionContext> bag = new MutableBag<>();
        Mapper mapper = new Mapper(config -> config.addProjector(
            User.class,
            UserView.class,
            (s, d) -> bag::setValue));

        mapper.map(source, destination, User.class, UserView.class);

        ProjectionContext actual = bag.getValue();
        assertThat(actual).isNotNull();
        assertThat(actual.getMapper()).isSameAs(mapper);
        assertThat(actual.getSourceType()).isSameAs(User.class);
        assertThat(actual.getDestinationType()).isSameAs(UserView.class);
    }

    @AutoParameterizedTest
    void addProjector_with_classes_configures_projection(
        User source,
        UserView destination
    ) {
        Mapper mapper = new Mapper(Projector_specs::configureUserProjector);

        mapper.map(source, destination, User.class, UserView.class);

        assertThat(destination.getId()).isEqualTo(valueOf(source.getId()));
        assertThat(destination.getName()).isEqualTo(source.getUsername());
    }

    @AllArgsConstructor
    @Getter
    public static class UserBag {
        private final User value;
    }

    @Getter
    @Setter
    public static class MutableUserViewBag {
        private UserView value;
    }

    @AutoParameterizedTest
    void addProjector_with_classes_configures_projection_for_writable_properties(
        UserBag source,
        UserView userView
    ) {
        Mapper mapper = new Mapper(Projector_specs::configureUserProjector);
        MutableUserViewBag destination = new MutableUserViewBag();
        destination.setValue(userView);

        mapper.map(source, destination, UserBag.class, MutableUserViewBag.class);

        UserView actual = destination.getValue();
        assertThat(actual).isSameAs(userView);
        assertThat(actual.getId()).isEqualTo(valueOf(source.getValue().getId()));
        assertThat(actual.getName()).isEqualTo(source.getValue().getUsername());
    }

    @AutoParameterizedTest
    void addProjector_with_classes_creates_new_instance_if_value_of_writable_destination_property_is_null(
        UserBag source
    ) {
        Mapper mapper = new Mapper(Projector_specs::configureUserProjector);
        MutableUserViewBag destination = new MutableUserViewBag();
        destination.setValue(null);

        mapper.map(source, destination, UserBag.class, MutableUserViewBag.class);

        UserView actual = destination.getValue();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(valueOf(source.getValue().getId()));
        assertThat(actual.getName()).isEqualTo(source.getValue().getUsername());
    }

    @AllArgsConstructor
    @Getter
    public static class ImmutableUserViewBag {
        private final UserView value;
    }

    @AutoParameterizedTest
    void addProjector_with_classes_configures_projection_for_read_only_properties(
        UserBag source,
        ImmutableUserViewBag destination
    ) {
        Mapper mapper = new Mapper(Projector_specs::configureUserProjector);

        mapper.map(source, destination, UserBag.class, ImmutableUserViewBag.class);

        UserView actual = destination.getValue();
        assertThat(actual.getId()).isEqualTo(valueOf(source.getValue().getId()));
        assertThat(actual.getName()).isEqualTo(source.getValue().getUsername());
    }

    @AutoParameterizedTest
    void addProjector_with_classes_throws_exception_if_value_of_read_only_destination_property_is_null(
        UserBag source
    ) {
        // Arrange
        Mapper mapper = new Mapper(Projector_specs::configureUserProjector);
        ImmutableUserViewBag destination = new ImmutableUserViewBag(null);

        // Act
        ThrowingCallable action = () -> mapper.map(
            source,
            destination,
            UserBag.class,
            ImmutableUserViewBag.class);

        // Assert
        assertThatThrownBy(action)
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("value");
    }

    @Test
    void addProjector_with_classes_does_not_throw_exception_if_value_of_read_only_destination_property_is_null_and_value_of_source_property_is_null() {
        // Arrange
        Mapper mapper = new Mapper(Projector_specs::configureUserProjector);
        UserBag source = new UserBag(null);
        ImmutableUserViewBag destination = new ImmutableUserViewBag(null);

        // Act
        Executable action = () -> mapper.map(
            source,
            destination,
            UserBag.class,
            ImmutableUserViewBag.class);

        // Assert
        assertDoesNotThrow(action);
    }

    @AutoParameterizedTest
    void addProjector_with_classes_throws_exception_if_value_of_read_only_destination_property_is_not_null_and_value_of_source_property_is_null(
        ImmutableUserViewBag destination
    ) {
        // Arrange
        Mapper mapper = new Mapper(Projector_specs::configureUserProjector);
        UserBag source = new UserBag(null);

        // Act
        ThrowingCallable action = () -> mapper.map(
            source,
            destination,
            UserBag.class,
            ImmutableUserViewBag.class);

        // Assert
        assertThatThrownBy(action)
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("value");
    }

    @AutoParameterizedTest
    void addProjector_with_classes_overrides_existing_projector(
        User source,
        UserView destination
    ) {
        Mapper sut = new Mapper(config -> {
            configureUserProjector(config, (s, d) -> d.setId(valueOf(s.getId() + 1)));
            configureUserProjector(config, (s, d) -> d.setId(valueOf(s.getId())));
        });

        sut.map(source, destination, User.class, UserView.class);

        assertThat(destination.getId()).isEqualTo(valueOf(source.getId()));
    }

    @Test
    void addProjector_with_type_predicates_is_fluent() {
        new Mapper(config -> assertThat(
            config
                .addProjector(
                    type -> type.equals(User.class),
                    type -> type.equals(UserView.class),
                    (source, destination) -> context -> {}))
            .isSameAs(config));
    }

    @Test
    void addProjector_with_type_predicates_has_null_guard_for_source_type_predicate() {
        new Mapper(config -> assertThatThrownBy(
            () -> config
                .addProjector(
                    null,
                    type -> type.equals(UserView.class),
                    (source, destination) -> context -> {}))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("sourceTypePredicate"));
    }

    @Test
    void addProjector_with_type_predicates_has_null_guard_for_destination_type_predicate() {
        new Mapper(config -> assertThatThrownBy(
            () -> config
                .addProjector(
                    type -> type.equals(User.class),
                    null,
                    (source, destination) -> context -> {}))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("destinationTypePredicate"));
    }

    @Test
    void addProjector_with_type_predicates_has_null_guard_for_function() {
        new Mapper(config -> assertThatThrownBy(
            () -> config
                .addProjector(
                    type -> type.equals(User.class),
                    type -> type.equals(UserView.class),
                    null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("consumer"));
    }

    @Test
    void addProjector_with_type_references_is_fluent() {
        new Mapper(config -> assertThat(
            config
                .addProjector(
                    new TypeReference<User>() {},
                    new TypeReference<UserView>() {},
                    (source, destination) -> context -> {}))
            .isSameAs(config));
    }

    @Test
    void addProjector_with_type_references_has_null_guard_for_source_type_reference() {
        new Mapper(config -> assertThatThrownBy(
            () -> config
                .addProjector(
                    null,
                    new TypeReference<UserView>() {},
                    (source, destination) -> context -> {}))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("sourceTypeReference"));
    }

    @Test
    void addProjector_with_type_references_has_null_guard_for_destination_type_reference() {
        new Mapper(config -> assertThatThrownBy(
            () -> config
                .addProjector(
                    new TypeReference<User>() {},
                    null,
                    (source, destination) -> context -> {}))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("destinationTypeReference"));
    }

    @Test
    void addProjector_with_type_references_has_null_guard_for_function() {
        new Mapper(config -> assertThatThrownBy(
            () -> config
                .addProjector(
                    new TypeReference<User>() {},
                    new TypeReference<UserView>() {},
                    null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("consumer"));
    }

    @AutoParameterizedTest
    void addProjector_with_type_references_configures_projection(
        User source,
        UserView destination
    ) {
        Mapper mapper = new Mapper(config -> config.addProjector(
            new TypeReference<User>() {},
            new TypeReference<UserView>() {},
            (user, userView) -> context -> {
                userView.setId(String.valueOf(user.getId()));
                userView.setName(user.getUsername());
            }));

        mapper.map(source, destination, User.class, UserView.class);

        assertThat(destination.getId()).isEqualTo(valueOf(source.getId()));
        assertThat(destination.getName()).isEqualTo(source.getUsername());
    }

    private static void configureUserProjector(
        MapperConfiguration config,
        BiConsumer<User, UserView> projector
    ) {
        config.addProjector(
            User.class,
            UserView.class,
            (user, userView) -> context -> projector.accept(user, userView));
    }

    private static void configureUserProjector(MapperConfiguration config) {
        configureUserProjector(config, (user, userView) -> {
            userView.setId(valueOf(user.getId()));
            userView.setName(user.getUsername());
        });
    }
}
