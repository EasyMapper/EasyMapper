package test.easymapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class User {

    private long id;
    private String username;
    private String email;
    private String passwordHash;
    private String firstName;
    private String middleName;
    private String lastName;
}
