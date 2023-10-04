package test.easymapper.fixture;

import lombok.Getter;
import java.beans.ConstructorProperties;

@Getter
public class Employee extends User {

    private final long departmentId;

    @ConstructorProperties({
        "id",
        "username",
        "passwordHash",
        "departmentId"
    })
    public Employee(
        long id,
        String username,
        String passwordHash,
        long departmentId
    ) {
        super(id, username, passwordHash);
        this.departmentId = departmentId;
    }
}
