package test.easymapper;

import java.beans.ConstructorProperties;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import easymapper.Mapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SpecsForConstructorSelection {

    @AllArgsConstructor
    @Getter
    public static class User {

        private final long id;
        private final String username;
        private final String passwordHash;
    }

    @SuppressWarnings("unused")
    @NoArgsConstructor
    @Getter
    @Entity
    public static class UserEntity {

        @Id
        private long id;

        @Column(unique = true)
        private String username;

        @Column(unique = true)
        @Setter
        private String passwordHash;

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

    @Test
    @AutoDomainParams
    void convert_chooses_constructor_with_most_parameters(
        Mapper sut,
        User source
    ) {
        UserEntity actual = sut.convert(source, UserEntity.class);
        assertThat(actual.getId()).isEqualTo(source.getId());
    }
}
