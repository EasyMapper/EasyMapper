package test.easymapper.fixture;

import lombok.Getter;
import java.beans.ConstructorProperties;

@Getter
public class HasBrokenConstructor {

    public static final String DEFAULT_USERNAME = "Obi-Wan Kenobi";

    private final long id;
    private final String username;

    @ConstructorProperties("id")
    public HasBrokenConstructor(long id) {
        this.id = id;
        this.username = DEFAULT_USERNAME;
    }

    @ConstructorProperties({"id", "username"})
    public HasBrokenConstructor(long id, String username) {
        throw new RuntimeException("Broken constructor");
    }
}
