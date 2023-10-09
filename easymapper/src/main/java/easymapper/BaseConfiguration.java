package easymapper;

class BaseConfiguration {

    public static void configure(MapperConfiguration config) {
        config
            .apply(PrimitiveMapping::configure)
            .apply(SimpleObjectMapping::configure)
            .apply(CollectionMapping::configure);
    }
}
