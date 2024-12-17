package test.easymapper;

import autoparams.AutoSource;
import autoparams.customization.Customization;
import autoparams.mockito.MockitoCustomizer;
import autoparams.processor.InstancePropertyWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.params.ParameterizedTest;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ParameterizedTest
@AutoSource
@Customization({
    MockitoCustomizer.class,
    InstancePropertyWriter.class
})
public @interface AutoParameterizedTest {
}
