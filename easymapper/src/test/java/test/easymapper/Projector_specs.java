package test.easymapper;

import easymapper.Mapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

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
                .project(context -> (source, destination) -> {}))
                .isSameAs(mapping)));
    }

    @AutoParameterizedTest
    void project_correctly_works(User user, UserView view) {
        Mapper mapper = new Mapper(config -> config
            .map(User.class, UserView.class, mapping -> mapping
                .project(context -> (source, destination) -> {
                    destination.setId(valueOf(source.getId()));
                    destination.setName(source.getUsername());
                })));

        mapper.map(user, view, User.class, UserView.class);

        assertThat(view.getId()).isEqualTo(valueOf(user.getId()));
        assertThat(view.getName()).isEqualTo(user.getUsername());
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
    void project_projects_to_existing_value_of_writable_destination_properties(
        UserBag userBag,
        UserView userView
    ) {
        // Arrange
        Mapper mapper = new Mapper(config -> config
            .map(User.class, UserView.class, mapping -> mapping
                .project(context -> (source, destination) -> {
                    destination.setId(valueOf(source.getId()));
                    destination.setName(source.getUsername());
                })));

        MutableUserViewBag userViewBag = new MutableUserViewBag();
        userViewBag.setValue(userView);

        // Act
        mapper.map(
            userBag,
            userViewBag,
            UserBag.class,
            MutableUserViewBag.class);

        // Assert
        UserView actual = userViewBag.getValue();
        assertThat(actual).isSameAs(userView);
        assertThat(actual.getId())
            .isEqualTo(valueOf(userBag.getValue().getId()));
        assertThat(actual.getName())
            .isEqualTo(userBag.getValue().getUsername());
    }

    @AutoParameterizedTest
    void project_creates_new_instance_if_writable_destination_property_is_null(
        UserBag userBag
    ) {
        // Arrange
        Mapper mapper = new Mapper(config -> config
            .map(User.class, UserView.class, mapping -> mapping
                .project(context -> (source, destination) -> {
                    destination.setId(valueOf(source.getId()));
                    destination.setName(source.getUsername());
                })));

        MutableUserViewBag userViewBag = new MutableUserViewBag();
        userViewBag.setValue(null);

        // Act
        mapper.map(
            userBag,
            userViewBag,
            UserBag.class,
            MutableUserViewBag.class);

        // Assert
        UserView actual = userViewBag.getValue();
        assertThat(actual).isNotNull();
        assertThat(actual.getId())
            .isEqualTo(valueOf(userBag.getValue().getId()));
        assertThat(actual.getName())
            .isEqualTo(userBag.getValue().getUsername());
    }

    @AllArgsConstructor
    @Getter
    public static class ImmutableUserViewBag {
        private final UserView value;
    }

    @AutoParameterizedTest
    void project_projects_to_existing_value_of_read_only_destination_properties(
        UserBag userBag,
        ImmutableUserViewBag userViewBag
    ) {
        // Arrange
        Mapper mapper = new Mapper(config -> config
            .map(User.class, UserView.class, mapping -> mapping
                .project(context -> (source, destination) -> {
                    destination.setId(valueOf(source.getId()));
                    destination.setName(source.getUsername());
                })));

        // Act
        mapper.map(
            userBag,
            userViewBag,
            UserBag.class,
            ImmutableUserViewBag.class);

        // Assert
        UserView actual = userViewBag.getValue();
        assertThat(actual.getId())
            .isEqualTo(valueOf(userBag.getValue().getId()));
        assertThat(actual.getName())
            .isEqualTo(userBag.getValue().getUsername());
    }

    @AutoParameterizedTest
    void project_throws_exception_if_read_only_destination_property_is_null(
        UserBag userBag
    ) {
        // Arrange
        Mapper mapper = new Mapper(config -> config
            .map(User.class, UserView.class, mapping -> mapping
                .project(context -> (source, destination) -> {
                    destination.setId(valueOf(source.getId()));
                    destination.setName(source.getUsername());
                })));

        ImmutableUserViewBag userViewBag = new ImmutableUserViewBag(null);

        // Act
        ThrowingCallable action = () -> mapper.map(
            userBag,
            userViewBag,
            UserBag.class,
            ImmutableUserViewBag.class);

        // Assert
        assertThatThrownBy(action)
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("value");
    }

    @Test
    void project_does_not_throw_exception_if_read_only_destination_property_is_null_and_source_property_is_null() {
        // Arrange
        Mapper mapper = new Mapper(config -> config
            .map(User.class, UserView.class, mapping -> mapping
                .project(context -> (source, destination) -> {
                    destination.setId(valueOf(source.getId()));
                    destination.setName(source.getUsername());
                })));

        UserBag userBag = new UserBag(null);
        ImmutableUserViewBag userViewBag = new ImmutableUserViewBag(null);

        // Act
        Executable action = () -> mapper.map(
            userBag,
            userViewBag,
            UserBag.class,
            ImmutableUserViewBag.class);

        // Assert
        assertDoesNotThrow(action);
    }

    @AutoParameterizedTest
    void project_throws_exception_if_read_only_destination_property_is_not_null_and_source_property_is_null(
        ImmutableUserViewBag userViewBag
    ) {
        // Arrange
        Mapper mapper = new Mapper(config -> config
            .map(User.class, UserView.class, mapping -> mapping
                .project(context -> (source, destination) -> {
                    destination.setId(valueOf(source.getId()));
                    destination.setName(source.getUsername());
                })));

        UserBag userBag = new UserBag(null);

        // Act
        ThrowingCallable action = () -> mapper.map(
            userBag,
            userViewBag,
            UserBag.class,
            ImmutableUserViewBag.class);

        // Assert
        assertThatThrownBy(action)
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("value");
    }

    @Test
    void project_throws_exception_if_projection_already_set() {
        ThrowingCallable action = () -> new Mapper(config -> config
            .map(User.class, UserView.class, mapping -> mapping
                .project(context -> (source, destination) -> {})
                .project(context -> (source, destination) -> {})));

        assertThatThrownBy(action).isInstanceOf(RuntimeException.class);
    }
}
