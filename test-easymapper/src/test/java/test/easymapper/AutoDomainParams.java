package test.easymapper;

import java.lang.annotation.Retention;

import autoparams.AutoParams;
import autoparams.customization.Customization;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@AutoParams
@Customization(DomainCustomizer.class)
public @interface AutoDomainParams {
}
