package easymapper;

@FunctionalInterface
public interface Extractor<S> {

    Object extract(MappingContext context, S source);
}
