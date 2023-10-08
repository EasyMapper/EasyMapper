package easymapper;

class BaseConfiguration {

    public static void configurer(MapperConfiguration config) {
        config
            .apply(IdentityMapping::configurer)
            .apply(UUIDMapping::configurer)
            .apply(StringMapping::configurer)
            .apply(CollectionMapping::configurer);
    }
}
