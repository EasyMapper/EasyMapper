package test.easymapper;

import autoparams.customization.CompositeCustomizer;
import autoparams.processor.InstancePropertyWriter;

public class DomainCustomizer extends CompositeCustomizer {

    public DomainCustomizer() {
        super(new InstancePropertyWriter());
    }
}
