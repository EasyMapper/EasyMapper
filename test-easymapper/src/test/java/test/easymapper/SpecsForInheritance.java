package test.easymapper;

import java.beans.ConstructorProperties;

import easymapper.Mapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SpecsForInheritance {

    @AllArgsConstructor
    @Getter
    public static class User {

        private final long id;
        private final String username;
    }

    @Getter
    public static class Employee extends User {

        private final long departmentId;

        @ConstructorProperties({ "id", "username", "departmentId" })
        public Employee(long id, String username, long departmentId) {
            super(id, username);
            this.departmentId = departmentId;
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class UserView {

        private long id;
        private String username;
    }

    @Getter
    @Setter
    public static class EmployeeView extends UserView {

        private long departmentId;
    }

    @Test
    @AutoDomainParams
    void convert_correctly_sets_inherited_properties_through_constructors(
        Mapper sut,
        EmployeeView view
    ) {
        Employee actual = sut.convert(view, Employee.class);
        assertThat(actual.getUsername()).isEqualTo(view.getUsername());
    }

    @Test
    @AutoDomainParams
    void convert_correctly_sets_inherited_properties_through_setters(
        Mapper sut,
        Employee source
    ) {
        EmployeeView actual = sut.convert(source, EmployeeView.class);
        assertThat(actual.getUsername()).isEqualTo(source.getUsername());
    }
}
