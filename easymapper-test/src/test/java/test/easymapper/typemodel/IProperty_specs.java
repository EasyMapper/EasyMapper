package test.easymapper.typemodel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.params.ParameterizedTest;

import autoparams.AutoSource;
import autoparams.customization.Customization;
import autoparams.mockito.MockitoCustomizer;
import easymapper.typemodel.IProperty;

class IProperty_specs {

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void tryGetValue_returns_value_when_property_is_gettable(
        IProperty property,
        Object instance,
        Object value
    ) {
        when(property.isGettable()).thenReturn(true);
        when(property.getValue(instance)).thenReturn(value);

        Optional<Supplier<Object>> actual = property.tryGetValue(instance);

        assertThat(actual).isNotEmpty();
        assertThat(actual.get().get()).isSameAs(value);
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void tryGetValue_does_not_work_in_lazy_manner(
        IProperty property,
        Object instance,
        Object value,
        Object newValue
    ) {
        when(property.isGettable()).thenReturn(true);
        when(property.getValue(instance)).thenReturn(value);

        Optional<Supplier<Object>> actual = property.tryGetValue(instance);
        when(property.getValue(instance)).thenReturn(newValue);

        assertThat(actual.get().get()).isSameAs(value);
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void tryGetValue_returns_empty_when_property_is_not_gettable(
        IProperty property,
        Object instance
    ) {
        when(property.isGettable()).thenReturn(false);
        Optional<Supplier<Object>> actual = property.tryGetValue(instance);
        assertThat(actual).isEmpty();
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void trySetValue_returns_true_when_property_is_settable(
        IProperty property,
        Object instance,
        Object value
    ) {
        when(property.isSettable()).thenReturn(true);
        boolean actual = property.trySetValue(instance, value);
        assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void trySetValue_sets_value_when_property_is_settable(
        IProperty property,
        Object instance,
        Object value
    ) {
        when(property.isSettable()).thenReturn(true);
        property.trySetValue(instance, value);
        verify(property, times(1)).setValue(instance, value);
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void trySetValue_returns_false_when_property_is_not_settable(
        IProperty property,
        Object instance,
        Object value
    ) {
        when(property.isSettable()).thenReturn(false);
        boolean actual = property.trySetValue(instance, value);
        assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void trySetValue_does_not_set_value_when_property_is_not_settable(
        IProperty property,
        Object instance,
        Object value
    ) {
        when(property.isSettable()).thenReturn(false);
        property.trySetValue(instance, value);
        verify(property, never()).setValue(instance, value);
    }
}
