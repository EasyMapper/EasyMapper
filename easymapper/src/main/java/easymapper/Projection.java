package easymapper;

@FunctionalInterface
public interface Projection<S, D> {

    void project(MappingContext context, S source, D destination);

    static <S, D> Projection<S, D> empty() {
        return (context, source, destination) -> {};
    }
}