package test.easymapper;

import easymapper.ParameterNameResolver;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("DataFlowIssue")
public class ParameterNameResolver_specs {

    @AutoParameterizedTest
    void tryResolveNames_has_null_guard_for_constructor(
        ParameterNameResolver sut
    ) {
        assertThatThrownBy(() -> sut.tryResolveNames(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("constructor");
    }
}
