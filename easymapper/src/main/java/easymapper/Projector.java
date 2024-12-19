package easymapper;

@FunctionalInterface
public interface Projector<S, T> {

    void project(MappingContext context, S source, T target);

    static <S, T> Projector<S, T> empty() {
        return (context, source, target) -> {};
    }

    default Runnable bind(MappingContext context, S source, T target) {
        return () -> project(context, source, target);
    }
}
