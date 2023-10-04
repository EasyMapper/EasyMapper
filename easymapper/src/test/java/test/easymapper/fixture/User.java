package test.easymapper.fixture;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class User {
    private final long id;
    private final String username;
    private final String passwordHash;
}
