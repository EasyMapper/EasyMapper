package test.easymapper;

import autoparams.ObjectQuery;
import autoparams.ResolutionContext;
import autoparams.customization.Customizer;
import autoparams.customization.CustomizerFactory;
import autoparams.generator.ObjectContainer;
import autoparams.generator.ObjectGenerator;
import org.junit.jupiter.params.support.AnnotationConsumer;

public class NullCustomizer implements
    CustomizerFactory,
    ObjectGenerator,
    AnnotationConsumer<UseNull> {

    private Class<?> type;

    @Override
    public Customizer createCustomizer() {
        return this;
    }

    @Override
    public ObjectContainer generate(
        ObjectQuery query,
        ResolutionContext context
    ) {
        return query.getType().equals(type)
            ? new ObjectContainer(null)
            : ObjectContainer.EMPTY;
    }

    @Override
    public void accept(UseNull annotation) {
        type = annotation.value();
    }
}
