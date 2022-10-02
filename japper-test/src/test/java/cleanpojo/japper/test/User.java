package cleanpojo.japper.test;

import java.beans.ConstructorProperties;

public final class User {

    private long id;
    private String username;
    private String passwordHash;

    @ConstructorProperties({ "id", "username", "passwordHash" })
    public User(long id, String username, String passwordHash) {
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
