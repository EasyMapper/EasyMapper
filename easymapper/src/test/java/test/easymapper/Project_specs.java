package test.easymapper;

import easymapper.Mapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

import static java.lang.String.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class Project_specs {

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
            UserViewBag.class);

        // Assert
        UserView actual = userViewBag.getValue();
        assertThat(actual.getId())
            .isEqualTo(valueOf(userBag.getValue().getId()));
        assertThat(actual.getName())
            .isEqualTo(userBag.getValue().getUsername());
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
