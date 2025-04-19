package test.easymapper;

import autoparams.customization.CustomizerSource;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@CustomizerSource(NullCustomizer.class)
public @interface UseNull {

    Class<?> value();
}
