package test.easymapper.fixture;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class User {
    private long id;
    private String username;
    private String passwordHash;
}
