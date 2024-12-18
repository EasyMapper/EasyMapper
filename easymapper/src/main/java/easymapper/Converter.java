package easymapper;

@FunctionalInterface
public interface Converter<S, D> {

    D convert(MappingContext context, S source);
}
