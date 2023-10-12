package test.easymapper;

import easymapper.Mapper;
import java.beans.ConstructorProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static org.assertj.core.api.Assertions.assertThat;

public class Inheritance_specs {

    @AllArgsConstructor
    @Getter
    public static class User {
        private final long id;
        private final String username;
        private final String passwordHash;
    }

    @Getter
    @Setter
    public static class UserView {
        private long id;
        private String username;
    }

    @Getter
    public static class Employee extends User {

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

    @Getter
    @Setter
    public static class EmployeeView extends UserView {
        private long departmentId;
    }

    @AutoParameterizedTest
    void sut_correctly_sets_inherited_properties_through_setters(
        Mapper sut,
        Employee source
    ) {
        EmployeeView actual = sut.map(
            source,
            Employee.class,
            EmployeeView.class);

        assertThat(actual.getUsername()).isEqualTo(source.getUsername());
    }

    @AutoParameterizedTest
    void sut_correctly_sets_inherited_properties_through_constructors(
        EmployeeView view
    ) {
        Mapper sut = new Mapper(config -> config
            .map(EmployeeView.class, Employee.class, mapping -> mapping
                .compute("passwordHash", source -> context -> null)));

        Employee actual = sut.map(view, EmployeeView.class, Employee.class);

        assertThat(actual.getUsername()).isEqualTo(view.getUsername());
    }
}
