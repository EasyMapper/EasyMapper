package easymapper;

class BaseConfiguration {

    public static void configure(MapperConfigurationBuilder config) {
        config
            .apply(PrimitiveObjectMapping::configure)
            .apply(AtomicObjectMapping::configure)
            .apply(UUIDMapping::configure)
            .apply(CollectionMapping::configure);
    }
}
