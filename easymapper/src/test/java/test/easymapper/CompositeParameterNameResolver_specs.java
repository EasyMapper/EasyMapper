package test.easymapper;

import java.lang.reflect.Parameter;
import java.util.Optional;

import easymapper.CompositeParameterNameResolver;
import easymapper.ParameterNameResolver;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({ "ConstantValue", "DataFlowIssue" })
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
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void sut_has_guard_against_null_resolver() {
        assertThatThrownBy(() -> new CompositeParameterNameResolver(null, null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void tryResolveName_has_guard_against_null_parameter() {
        val sut = new CompositeParameterNameResolver();

        assertThatThrownBy(() -> sut.tryResolveName(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("parameter");
    }

    @AutoParameterizedTest
    void tryResolveName_returns_name_from_first_resolver(
        String first,
        String second
    ) {
        // Arrange
        val sut = new CompositeParameterNameResolver(
            parameter -> Optional.of(first),
            parameter -> Optional.of(second)
        );

        Parameter parameter = CompositeParameterNameResolver
            .class
            .getConstructors()[0]
            .getParameters()[0];

        // Act
        Optional<String> actual = sut.tryResolveName(parameter);

        // Assert
        assertThat(actual).contains(first);
    }

    @AutoParameterizedTest
    void tryResolveName_returns_name_from_second_resolver_if_first_resolver_fails(
        String name
    ) {
        // Arrange
        val sut = new CompositeParameterNameResolver(
            parameter -> Optional.empty(),
            parameter -> Optional.of(name)
        );

        Parameter parameter = CompositeParameterNameResolver
            .class
            .getConstructors()[0]
            .getParameters()[0];

        // Act
        Optional<String> actual = sut.tryResolveName(parameter);

        // Assert
        assertThat(actual).contains(name);
    }
}
