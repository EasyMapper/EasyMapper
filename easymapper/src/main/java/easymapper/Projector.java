package easymapper;

@FunctionalInterface
public interface Projector<S, T> {

    void project(S source, T target, MappingContext context);

    static <S, T> Projector<S, T> empty() {
        return (source, target, context) -> {};
    }
}
