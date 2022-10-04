package easymapper.test;

import java.beans.ConstructorProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public final class UserEntity {

    @Id
    private long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String passwordHash;

    @Version
    private int version;

    @ConstructorProperties({ "username", "passwordHash" })
    public UserEntity(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    @ConstructorProperties({ "id", "username", "passwordHash" })
    public UserEntity(long id, String username, String passwordHash) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
