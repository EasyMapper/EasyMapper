package easymapper;

class StringMapping {

    public static void configurer(MapperConfiguration config) {
        config.addProjector(
            String.class,
            String.class,
            (source, target) -> context -> {});
    }
}
