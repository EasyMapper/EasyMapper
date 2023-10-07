package test.easymapper;

import easymapper.Mapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Mapper_specs {

    @Test
    void constructor_has_guard_against_null_configurer() {
        assertThatThrownBy(() -> new Mapper(null))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
