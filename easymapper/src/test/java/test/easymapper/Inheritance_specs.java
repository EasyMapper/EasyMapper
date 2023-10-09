package test.easymapper;

import easymapper.Mapper;
import test.easymapper.fixture.Employee;
import test.easymapper.fixture.EmployeeView;

import static org.assertj.core.api.Assertions.assertThat;

public class Inheritance_specs {

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
        EmployeeView source
    ) {
        Mapper sut = new Mapper(config -> config
            .addPropertyMapping(EmployeeView.class, Employee.class, mapping -> mapping
                .set("passwordHash", x -> null)));

        Employee actual = sut.map(source, EmployeeView.class, Employee.class);

        assertThat(actual.getUsername()).isEqualTo(source.getUsername());
    }
}
