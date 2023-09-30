package test.easymapper;

import easymapper.CompositeParameterNameResolver;
import easymapper.ParameterNameResolver;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CompositeParameterNameResolver_specs {

    @Test
    void sut_implements_ParameterNameResolver() {
        assertThat(ParameterNameResolver.class)
            .isAssignableFrom(CompositeParameterNameResolver.class);
    }

    @Test
    void sut_has_guard_against_null_resolvers() {
        ParameterNameResolver[] resolvers = null;
        assertThatThrownBy(() -> new CompositeParameterNameResolver(resolvers))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void sut_has_guard_against_null_resolver() {
        assertThatThrownBy(() -> new CompositeParameterNameResolver(null, null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @AutoParameterizedTest
    void tryResolveName_returns_name_from_first_resolver(
        String first,
        String second
    ) {
        CompositeParameterNameResolver sut = new CompositeParameterNameResolver(
            parameter -> Optional.of(first),
            parameter -> Optional.of(second));

        Optional<String> actual = sut.tryResolveName(null);

        assertThat(actual).contains(first);
    }

    @AutoParameterizedTest
    void tryResolveName_returns_name_from_second_resolver_if_first_resolver_fails(
        String name
    ) {
        CompositeParameterNameResolver sut = new CompositeParameterNameResolver(
            parameter -> Optional.empty(),
            parameter -> Optional.of(name));

        Optional<String> actual = sut.tryResolveName(null);

        assertThat(actual).contains(name);
    }
}
