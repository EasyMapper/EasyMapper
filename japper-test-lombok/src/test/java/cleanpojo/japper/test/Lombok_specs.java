package cleanpojo.japper.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.javaunit.autoparams.AutoSource;
import org.junit.jupiter.params.ParameterizedTest;

import cleanpojo.japper.Mapper;

class Lombok_specs {

    @ParameterizedTest
    @AutoSource
    void sut_correctly_maps_object_using_lombok(Mapper sut, User source) {
        UserView actual = sut.map(source, UserView.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }
}
