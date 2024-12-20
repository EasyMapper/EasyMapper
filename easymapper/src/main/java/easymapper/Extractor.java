package easymapper;

@FunctionalInterface
public interface Extractor<S, P> {

    P extract(MappingContext context, S source);
}
