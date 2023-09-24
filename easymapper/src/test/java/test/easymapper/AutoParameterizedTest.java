package test.easymapper;

import autoparams.AutoSource;
import org.junit.jupiter.params.ParameterizedTest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ParameterizedTest
@AutoSource
public @interface AutoParameterizedTest {
}
