package test.easymapper;

import easymapper.Mapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({ "DataFlowIssue" })
class SpecsForConfiguration {

    @Test
    void constructor_has_guard_against_null_configure() {
        assertThatThrownBy(() -> new Mapper(null))
            .isInstanceOf(NullPointerException.class);
    }
}
