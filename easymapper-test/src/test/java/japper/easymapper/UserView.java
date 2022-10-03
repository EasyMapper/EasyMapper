package japper.easymapper;

import java.beans.ConstructorProperties;

public final class UserView {

    private final long id;
    private final String username;

    @ConstructorProperties({ "id", "username" })
    public UserView(long id, String username) {
        this.id = id;
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
