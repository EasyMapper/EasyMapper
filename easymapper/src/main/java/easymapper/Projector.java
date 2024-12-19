package easymapper;

@FunctionalInterface
public interface Projector<S, T> {

    void project(MappingContext context, S source, T target);
}
