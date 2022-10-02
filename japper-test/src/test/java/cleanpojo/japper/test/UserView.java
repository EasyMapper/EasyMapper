package cleanpojo.japper.test;

import java.beans.ConstructorProperties;

public final class UserView {

    private long id;
    private String username;

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
