package easymapper.test;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class UserView {

    private long id;
    private String username;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
}
