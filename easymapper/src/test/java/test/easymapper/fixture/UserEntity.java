package test.easymapper.fixture;

import java.beans.ConstructorProperties;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
public class UserEntity {

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
