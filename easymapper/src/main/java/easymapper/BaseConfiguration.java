package easymapper;

class BaseConfiguration {

    public static void configure(MapperConfiguration config) {
        config
            .apply(PrimitiveObjectMapping::configure)
            .apply(AtomicObjectMapping::configure)
            .apply(UUIDMapping::configure)
            .apply(CollectionMapping::configure);
    }
}
