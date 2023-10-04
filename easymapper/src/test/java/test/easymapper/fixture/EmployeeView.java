package test.easymapper.fixture;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeView extends UserView {
    private long departmentId;
}
