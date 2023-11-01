package test.easymapper;

import easymapper.Mapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

import static java.lang.String.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class Convert_specs {

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

        public static UserView from(User user) {
            return new UserView(valueOf(user.getId()), user.getUsername());
        }
    }

    @Test
    void convert_has_null_guard_for_function() {
        assertThatThrownBy(() -> new Mapper(config -> config
            .map(User.class, UserView.class, mapping -> mapping
                .convert(null))))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("function");
    }

    @Test
    void convert_is_fluent() {
        new Mapper(config -> config
            .map(User.class, UserView.class, mapping -> assertThat(mapping
                .convert(context -> UserView::from))
                .isSameAs(mapping)));
    }

    @AutoParameterizedTest
    void convert_correctly_works(User user) {
        Mapper mapper = new Mapper(config -> config
            .map(User.class, UserView.class, mapping -> mapping
                .convert(context -> UserView::from)));

        UserView actual = mapper.map(user, User.class, UserView.class);

        assertThat(actual.getId()).isEqualTo(valueOf(user.getId()));
        assertThat(actual.getName()).isEqualTo(user.getUsername());
    }

    @Test
    void convert_throws_exception_if_conversion_already_set() {
        ThrowingCallable action = () -> new Mapper(config -> config
            .map(User.class, UserView.class, mapping -> mapping
                .convert(context -> UserView::from)
                .convert(context -> UserView::from)));

        assertThatThrownBy(action).isInstanceOf(RuntimeException.class);
    }
}
