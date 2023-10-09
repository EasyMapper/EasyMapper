package test.easymapper.fixture;

import lombok.Getter;
import java.beans.ConstructorProperties;

@Getter
public class HasBrokenConstructor {

    public static final String DEFAULT_USERNAME = "Obi-Wan Kenobi";

    private final int id;
    private final String username;

    @ConstructorProperties("id")
    public HasBrokenConstructor(int id) {
        this.id = id;
        this.username = DEFAULT_USERNAME;
    }

    @ConstructorProperties({"id", "username"})
    public HasBrokenConstructor(int id, String username) {
        throw new RuntimeException("Broken constructor");
    }
}
