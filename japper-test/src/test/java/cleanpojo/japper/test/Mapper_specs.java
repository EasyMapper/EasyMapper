package cleanpojo.japper.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.javaunit.autoparams.AutoSource;
import org.junit.jupiter.params.ParameterizedTest;

import cleanpojo.japper.Mapper;

class Mapper_specs {

    @ParameterizedTest
    @AutoSource
    void sut_correctly_maps_object(Mapper sut, User source) {
        User actual = sut.map(source, User.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }

    @ParameterizedTest
    @AutoSource
    void sut_ignores_extra_properties(Mapper sut, User source) {
        UserView actual = sut.map(source, UserView.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }
}
