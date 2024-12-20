package easymapper;

import java.util.function.Supplier;

@FunctionalInterface
interface ObjectExtractor extends Extractor<Object, Object> {

    @SuppressWarnings("unchecked")
    static <S, P> ObjectExtractor from(Extractor<S, P> extractor) {
        return (context, source) -> extractor.extract(context, (S) source);
    }

    default Supplier<Object> bindAll(MappingContext context, Object source) {
        return () -> extract(context, source);
    }
}
