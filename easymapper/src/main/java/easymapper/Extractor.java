package easymapper;

@FunctionalInterface
public interface Extractor<S, P> {

    P extract(S source, MappingContext context);
}
