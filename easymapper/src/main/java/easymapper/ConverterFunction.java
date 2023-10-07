package easymapper;

@FunctionalInterface
public interface ConverterFunction<S, D> {

    D convert(S source, ConversionContext context);
}
