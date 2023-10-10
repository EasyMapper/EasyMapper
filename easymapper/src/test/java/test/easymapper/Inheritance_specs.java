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
        EmployeeView view
    ) {
        Mapper sut = new Mapper(config -> config
            .map(EmployeeView.class, Employee.class, mapping -> mapping
                .compute("passwordHash", source -> context -> null)));

        Employee actual = sut.map(view, EmployeeView.class, Employee.class);

        assertThat(actual.getUsername()).isEqualTo(view.getUsername());
    }
}
