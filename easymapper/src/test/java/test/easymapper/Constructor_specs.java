package test.easymapper;

import easymapper.Mapper;
import java.beans.ConstructorProperties;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class Constructor_specs {

    @AllArgsConstructor
    @Getter
    public static class User {
        private final long id;
        private final String username;
        private final String passwordHash;
    }

    @NoArgsConstructor
    @Entity
    public static class UserEntity {

        @Getter
        @Id
        private long id;

        @Getter
        @Column(unique = true)
        private String username;

        @Getter
        @Setter
        @Column(unique = true)
        private String passwordHash;

        @Version
        private int version;

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
    void sut_chooses_constructor_with_most_parameters(Mapper sut, User source) {
        UserEntity actual = sut.map(source, User.class, UserEntity.class);
        assertThat(actual.getId()).isEqualTo(source.getId());
    }

    @Getter
    public static class UserView {

        private final long id;
        private final String name;

        public UserView(long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @AutoParameterizedTest
    void sut_fails_with_useful_message_if_constructor_not_decorated_with_constructor_properties_annotation(
        Mapper sut,
        UserView source
    ) {
        assertThatThrownBy(() -> sut.map(source, UserView.class, UserView.class))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContainingAll("UserView", "@ConstructorProperties");
    }
}
