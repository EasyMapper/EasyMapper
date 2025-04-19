package test.easymapper;

import autoparams.AutoSource;
import autoparams.customization.Customization;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@AutoSource
@Customization(DomainCustomizer.class)
public @interface AutoDomainSource {
}
