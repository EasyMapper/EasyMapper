package test.easymapper;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Consumer;

import easymapper.ConstructorExtractor;
import easymapper.Mapper;
import easymapper.MapperConfiguration;
import easymapper.ParameterNameResolver;
import easymapper.TypePredicate;
import easymapper.TypeReference;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

import static java.lang.String.valueOf;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("DataFlowIssue")
public class MapperConfiguration_specs {

    private static final TypePredicate acceptAll = type -> true;

    @AllArgsConstructor
    @Getter
    @Accessors(fluent = true)
    public static class User {

        private final int id;
        private final String username;
        private final String passwordHash;
    }

    @Test
    void addConverter_has_null_guard_for_source_type() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.addConverter(
                (Class<User>) null,
                User.class,
                (context, source) -> source
            )
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("sourceType");
    }

    @Test
    void addConverter_has_null_guard_for_target_type() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.addConverter(
                User.class,
                null,
                (context, source) -> source
            )
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("targetType");
    }

    @Test
    void addConverter_has_null_guard_for_converter() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.addConverter(User.class, User.class, null)
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("converter");
    }

    @Test
    void addConverter_is_fluent() {
        new Mapper(config -> {
            MapperConfiguration actual = config.addConverter(
                User.class,
                User.class,
                (context, source) -> source
            );
            assertThat(actual).isSameAs(config);
        });
    }

    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    @Setter
    @Accessors(fluent = true)
    public static class UserView {

        private String id;
        private String username;

        public static UserView from(User user) {
            return new UserView(valueOf(user.id()), user.username());
        }
    }

    @AutoParameterizedTest
    void addConverter_correctly_works(User user) {
        val mapper = new Mapper(
            config -> config.addConverter(
                User.class,
                UserView.class,
                (context, source) -> UserView.from(source)
            )
        );

        UserView actual = mapper.convert(user, UserView.class);

        assertThat(actual.id()).isEqualTo(valueOf(user.id()));
        assertThat(actual.username()).isEqualTo(user.username());
    }

    @AutoParameterizedTest
    void addConverter_correctly_works_for_parameter(User user) {
        val mapper = new Mapper(
            config -> config.addConverter(
                int.class,
                String.class,
                (context, source) -> valueOf(source)
            )
        );

        UserView actual = mapper.convert(user, UserView.class);

        assertThat(actual.id()).isEqualTo(valueOf(user.id()));
        assertThat(actual.username()).isEqualTo(user.username());
    }

    @AllArgsConstructor
    @Getter
    @Accessors(fluent = true)
    public static class Post {

        private final int id;
        private final int authorId;
        private final String title;
        private final String text;
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static class PostView {

        private String id;
        private String authorId;
        private String title;
        private String text;
    }

    @AutoParameterizedTest
    void addConverter_correctly_works_for_property(Post post) {
        val mapper = new Mapper(
            config -> config.addConverter(
                int.class,
                String.class,
                (context, source) -> valueOf(source)
            )
        );

        PostView actual = mapper.convert(post, PostView.class);

        assertThat(actual.id()).isEqualTo(valueOf(post.id()));
        assertThat(actual.authorId()).isEqualTo(valueOf(post.authorId()));
        assertThat(actual.title()).isEqualTo(post.title());
        assertThat(actual.text()).isEqualTo(post.text());
    }

    @AutoParameterizedTest
    void addConverter_overrides_previous_converter(
        Post post,
        String anonymous
    ) {
        val mapper = new Mapper(config -> config
            .addConverter(int.class, String.class, (c, s) -> anonymous)
            .addConverter(int.class, String.class, (c, s) -> s.toString())
        );

        PostView actual = mapper.convert(post, PostView.class);

        assertThat(actual.id()).isEqualTo(valueOf(post.id()));
        assertThat(actual.authorId()).isEqualTo(valueOf(post.authorId()));
    }

    @Test
    void addConverter_with_predicate_has_null_guard_for_source_type_predicate() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.addConverter(
                null,
                acceptAll,
                (context, source) -> source
            )
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("sourceTypePredicate");
    }

    @Test
    void addConverter_with_predicate_has_null_guard_for_target_type_predicate() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.addConverter(
                acceptAll,
                null,
                (context, source) -> source
            )
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("targetTypePredicate");
    }

    @Test
    void addConverter_with_predicate_has_null_guard_for_converter() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.addConverter(
                acceptAll,
                acceptAll,
                null
            )
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("converter");
    }

    @Test
    void addConverter_with_predicate_is_fluent() {
        new Mapper(config -> {
            MapperConfiguration actual = config.addConverter(
                acceptAll,
                acceptAll,
                (context, source) -> source
            );
            assertThat(actual).isSameAs(config);
        });
    }

    @AutoParameterizedTest
    void addConverter_with_predicate_correctly_works(User user) {
        val mapper = new Mapper(
            config -> config.<User, UserView>addConverter(
                type -> type.equals(User.class),
                type -> type.equals(UserView.class),
                (context, source) -> UserView.from(source)
            )
        );

        UserView actual = mapper.convert(user, UserView.class);

        assertThat(actual.id()).isEqualTo(valueOf(user.id()));
        assertThat(actual.username()).isEqualTo(user.username());
    }

    @Test
    void addProjector_has_null_guard_for_source_type() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.addProjector(
                (Class<User>) null,
                User.class,
                (context, source, target) -> { }
            )
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("sourceType");
    }

    @Test
    void addProjector_has_null_guard_for_target_type() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.addProjector(
                User.class,
                null,
                (context, source, target) -> { }
            )
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("targetType");
    }

    @Test
    void addProjector_has_null_guard_for_projector() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.addProjector(User.class, User.class, null)
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("projector");
    }

    @Test
    void addProjector_is_fluent() {
        new Mapper(config -> {
            MapperConfiguration actual = config.addProjector(
                User.class,
                UserView.class,
                (context, source, target) -> { }
            );
            assertThat(actual).isSameAs(config);
        });
    }

    @AutoParameterizedTest
    void addProjector_correctly_works(User user, UserView view) {
        val mapper = new Mapper(
            config -> config.addProjector(
                User.class,
                UserView.class,
                (context, source, target) -> {
                    target.id(valueOf(source.id()));
                    target.username(source.username());
                }
            )
        );

        mapper.project(user, view);

        assertThat(view.id()).isEqualTo(valueOf(user.id()));
        assertThat(view.username()).isEqualTo(user.username());
    }

    @AllArgsConstructor
    @Getter
    @Accessors(fluent = true)
    public static class UserBag {

        public final User value;
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static class UserViewBag {

        private UserView value;
    }

    @AutoParameterizedTest
    void addProjector_correctly_works_for_property(
        UserBag userBag,
        UserViewBag userViewBag
    ) {
        val mapper = new Mapper(
            config -> config.addProjector(
                User.class,
                UserView.class,
                (context, source, target) -> {
                    target.id(valueOf(source.id()));
                    target.username(source.username());
                }
            )
        );

        mapper.project(userBag, userViewBag);

        assertThat(userViewBag.value().id())
            .isEqualTo(valueOf(userBag.value().id()));
        assertThat(userViewBag.value().username())
            .isEqualTo(userBag.value().username());
    }

    @AutoParameterizedTest
    void addProjector_overrides_previous_projector(User user, UserView view) {
        val mapper = new Mapper(config -> config
            .addProjector(User.class, UserView.class, (c, s, t) -> { })
            .addProjector(
                User.class,
                UserView.class,
                (context, source, target) -> {
                    target.id(valueOf(source.id()));
                    target.username(source.username());
                }
            )
        );

        mapper.project(user, view);

        assertThat(view.id()).isEqualTo(valueOf(user.id()));
        assertThat(view.username()).isEqualTo(user.username());
    }

    @Test
    void addProjector_with_predicate_has_null_guard_for_source_type_predicate() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.addProjector(
                null,
                acceptAll,
                (context, source, target) -> { }
            )
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("sourceTypePredicate");
    }

    @Test
    void addProjector_with_predicate_has_null_guard_for_target_type_predicate() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.addProjector(
                acceptAll,
                null,
                (context, source, target) -> { }
            )
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("targetTypePredicate");
    }

    @Test
    void addProjector_with_predicate_has_null_guard_for_projector() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.addProjector(
                acceptAll,
                acceptAll,
                null
            )
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("projector");
    }

    @Test
    void addProjector_with_predicate_is_fluent() {
        new Mapper(config -> {
            MapperConfiguration actual = config.addProjector(
                acceptAll,
                acceptAll,
                (context, source, target) -> { }
            );
            assertThat(actual).isSameAs(config);
        });
    }

    @AutoParameterizedTest
    void addProjector_with_predicate_correctly_works(User user, UserView view) {
        val mapper = new Mapper(
            config -> config.<User, UserView>addProjector(
                type -> type.equals(User.class),
                type -> type.equals(UserView.class),
                (context, source, target) -> {
                    target.id(valueOf(source.id()));
                    target.username(source.username());
                }
            )
        );

        mapper.project(user, view);

        assertThat(view.id()).isEqualTo(valueOf(user.id()));
        assertThat(view.username()).isEqualTo(user.username());
    }

    @Test
    void addExtractor_has_null_guard_for_source_type() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.addExtractor(
                (Class<User>) null,
                UserView.class,
                "id",
                (context, source) -> valueOf(source.id())
            )
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("sourceType");
    }

    @Test
    void addExtractor_has_null_guard_for_target_type() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.addExtractor(
                User.class,
                null,
                "id",
                (context, source) -> valueOf(source.id())
            )
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("targetType");
    }

    @Test
    void addExtractor_has_null_guard_for_target_property_name() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.addExtractor(
                User.class,
                UserView.class,
                null,
                (context, source) -> valueOf(source.id())
            )
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("targetPropertyName");
    }

    @Test
    void addExtractor_has_null_guard_for_extractor() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.addExtractor(
                User.class,
                UserView.class,
                "id",
                null
            )
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("extractor");
    }

    @Test
    void addExtractor_is_fluent() {
        new Mapper(config -> {
            MapperConfiguration actual = config.addExtractor(
                User.class,
                UserView.class,
                "id",
                (context, source) -> valueOf(source.id())
            );
            assertThat(actual).isSameAs(config);
        });
    }

    @AllArgsConstructor
    @Getter
    @Accessors(fluent = true)
    public static class UserDto {

        private final int id;
        private final String name;
    }

    @AutoParameterizedTest
    void addExtractor_correctly_works_for_parameter(User user) {
        val mapper = new Mapper(
            config -> config.addExtractor(
                User.class,
                UserDto.class,
                "name",
                (context, source) -> source.username()
            )
        );

        UserDto actual = mapper.convert(user, UserDto.class);

        assertThat(actual.id()).isEqualTo(user.id());
        assertThat(actual.name()).isEqualTo(user.username());
    }

    @AutoParameterizedTest
    void addExtractor_correctly_works_for_property(User user) {
        val mapper = new Mapper(
            config -> config.addExtractor(
                User.class,
                UserView.class,
                "id",
                (context, source) -> valueOf(source.id())
            )
        );

        UserView actual = mapper.convert(user, UserView.class);

        assertThat(actual.id()).isEqualTo(valueOf(user.id()));
        assertThat(actual.username()).isEqualTo(user.username());
    }

    @AutoParameterizedTest
    void addExtractor_overrides_previous_extractor(User user) {
        val mapper = new Mapper(config -> config
            .<User, String>addExtractor(
                User.class,
                UserView.class,
                "id",
                (context, source) -> null
            )
            .addExtractor(
                User.class,
                UserView.class,
                "id",
                (context, source) -> valueOf(source.id())
            )
        );

        UserView actual = mapper.convert(user, UserView.class);

        assertThat(actual.id()).isEqualTo(valueOf(user.id()));
        assertThat(actual.username()).isEqualTo(user.username());
    }

    @Test
    void addExtractor_with_predicate_has_null_guard_for_source_type_predicate() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.addExtractor(
                null,
                acceptAll,
                "id",
                (context, source) -> null
            )
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("sourceTypePredicate");
    }

    @Test
    void addExtractor_with_predicate_has_null_guard_for_target_type_predicate() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.addExtractor(
                acceptAll,
                null,
                "id",
                (context, source) -> null
            )
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("targetTypePredicate");
    }

    @Test
    void addExtractor_with_predicate_has_null_guard_for_target_property_name() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.addExtractor(
                acceptAll,
                acceptAll,
                null,
                (context, source) -> null
            )
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("targetPropertyName");
    }

    @Test
    void addExtractor_with_predicate_has_null_guard_for_extractor() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.addExtractor(
                acceptAll,
                acceptAll,
                "id",
                null
            )
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("extractor");
    }

    @Test
    void addExtractor_with_predicate_is_fluent() {
        new Mapper(config -> {
            MapperConfiguration actual = config.addExtractor(
                acceptAll,
                acceptAll,
                "id",
                (context, source) -> null
            );
            assertThat(actual).isSameAs(config);
        });
    }

    @AutoParameterizedTest
    void addExtractor_with_predicate_correctly_works(User user) {
        val mapper = new Mapper(
            config -> config.<User, String>addExtractor(
                type -> type.equals(User.class),
                type -> type.equals(UserView.class),
                "id",
                (context, source) -> valueOf(source.id())
            )
        );

        UserView actual = mapper.convert(user, UserView.class);

        assertThat(actual.id()).isEqualTo(valueOf(user.id()));
        assertThat(actual.username()).isEqualTo(user.username());
    }

    @AllArgsConstructor
    @Getter
    public static class Pricing {

        private final double listPrice;
        private final double discount;

        public double calculateSalePrice() {
            return listPrice - discount;
        }
    }

    @AllArgsConstructor
    @Getter
    public static class PricingView {

        private final double listPrice;
        private final double discount;
        private final double salePrice;
    }

    @Test
    void map_with_predicates_has_null_guard_for_source_type_predicate() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.map(null, acceptAll, mapping -> { })
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("sourceTypePredicate");
    }

    @Test
    void map_with_predicates_has_null_guard_for_destination_type_predicate() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.map(acceptAll, null, mapping -> { })
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("destinationTypePredicate");
    }

    @Test
    void map_with_predicates_has_null_guard_for_mapping_configurer() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.map(acceptAll, acceptAll, null)
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("configurer");
    }

    @Test
    void map_with_predicates_is_fluent() {
        new Mapper(config -> {
            MapperConfiguration actual = config.map(
                acceptAll,
                acceptAll,
                mapping -> { }
            );
            assertThat(actual).isSameAs(config);
        });
    }

    @AutoParameterizedTest
    void map_with_predicates_correctly_works(User user) {
        Mapper mapper = new Mapper(config ->
            config.map(
                type -> type.equals(User.class),
                type -> type.equals(UserView.class),
                mapping -> mapping.convert(
                    (context, source) -> UserView.from((User) source)
                )
            )
        );

        UserView actual = mapper.convert(user, UserView.class);

        assertThat(actual.id()).isEqualTo(valueOf(user.id()));
        assertThat(actual.username()).isEqualTo(user.username());
    }

    @Test
    void map_with_classes_has_null_guard_for_source_type() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.map(null, int.class, mapping -> { })
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("sourceType");
    }

    @Test
    void map_with_classes_has_null_guard_for_destination_type() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.map(int.class, null, mapping -> { })
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("destinationType");
    }

    @Test
    void map_with_classes_has_null_guard_for_mapping_configurer() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.map(int.class, int.class, null)
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("configurer");
    }

    @Test
    void map_with_classes_overwrites_existing_map() {
        Mapper mapper = new Mapper(config -> config
            .map(
                String.class,
                String.class,
                mapping -> mapping.convert((context, source) -> source + "1")
            )
            .map(
                String.class,
                String.class,
                mapping -> mapping.convert((context, source) -> source + "2")
            )
        );

        String actual = mapper.convert("0", String.class);

        assertThat(actual).isEqualTo("02");
    }

    @Test
    void map_with_classes_is_fluent() {
        new Mapper(config -> {
            MapperConfiguration actual = config.map(
                int.class,
                int.class,
                mapping -> { }
            );
            assertThat(actual).isSameAs(config);
        });
    }

    @Test
    void map_with_type_references_has_null_guard_for_source_type() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.map(
                null,
                new TypeReference<Integer>() { },
                mapping -> { }
            )
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("sourceTypeReference");
    }

    @Test
    void map_with_type_references_has_null_guard_for_destination_type() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.map(
                new TypeReference<Integer>() { },
                null,
                mapping -> { }
            )
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("destinationTypeReference");
    }

    @Test
    void map_with_type_references_has_null_guard_for_mapping_configurer() {
        ThrowingCallable action = () -> new Mapper(
            config -> config.map(
                new TypeReference<Integer>() { },
                new TypeReference<Integer>() { },
                null
            )
        );

        assertThatThrownBy(action)
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("configurer");
    }

    @Test
    void map_with_type_references_is_fluent() {
        TypeReference<Integer> type = new TypeReference<Integer>() { };
        new Mapper(config -> {
            MapperConfiguration actual = config.map(type, type, mapping -> { });
            assertThat(actual).isSameAs(config);
        });
    }

    @AutoParameterizedTest
    void map_with_type_references_correctly_works(User user) {
        Mapper mapper = new Mapper(
            config -> config.map(
                new TypeReference<User>() { },
                new TypeReference<UserView>() { },
                mapping -> mapping.convert(
                    (context, source) -> UserView.from(source)
                )
            )
        );

        UserView actual = mapper.convert(user, UserView.class);

        assertThat(actual.id()).isEqualTo(valueOf(user.id()));
        assertThat(actual.username()).isEqualTo(user.username());
    }

    @AutoParameterizedTest
    void setConstructorExtractor_is_fluent() {
        new Mapper(config -> {
            MapperConfiguration actual = config.setConstructorExtractor(
                t -> emptyList()
            );
            assertThat(actual).isSameAs(config);
        });
    }

    @AutoParameterizedTest
    void setConstructorExtractor_has_guard_against_null_value() {
        ThrowingCallable callable = () -> new Mapper(
            config -> config.setConstructorExtractor(null)
        );

        assertThatThrownBy(callable).isInstanceOf(NullPointerException.class);
    }

    @SuppressWarnings("unused")
    @Getter
    public static class HasBrokenConstructor {

        public static final String DEFAULT_USERNAME = "Obi-Wan Kenobi";

        private final int id;
        private final String username;

        @ConstructorProperties("id")
        public HasBrokenConstructor(int id) {
            this.id = id;
            this.username = DEFAULT_USERNAME;
        }

        @ConstructorProperties({ "id", "username" })
        public HasBrokenConstructor(int id, String username) {
            throw new RuntimeException("Broken constructor");
        }
    }

    @AutoParameterizedTest
    void setConstructorExtractor_correctly_configures_extractor(User source) {
        // Arrange
        ConstructorExtractor extractor = type -> stream(type.getConstructors())
            .sorted(comparingInt(Constructor::getParameterCount))
            .limit(1)
            .collect(toList());
        Mapper mapper = new Mapper(c -> c.setConstructorExtractor(extractor));

        // Act
        HasBrokenConstructor actual = mapper.convert(
            source,
            HasBrokenConstructor.class
        );

        // Assert
        assertThat(actual.getId()).isEqualTo(source.id());
        assertThat(actual.getUsername())
            .isEqualTo(HasBrokenConstructor.DEFAULT_USERNAME);
    }

    @AutoParameterizedTest
    void setParameterNameResolver_is_fluent(String name) {
        new Mapper(config -> {
            MapperConfiguration actual = config.setParameterNameResolver(
                parameter -> Optional.of(name)
            );
            assertThat(actual).isSameAs(config);
        });
    }

    @Test
    void setParameterNameResolver_has_guard_against_null_value() {
        ThrowingCallable callable = () -> new Mapper(
            config -> config.setParameterNameResolver(null)
        );

        assertThatThrownBy(callable).isInstanceOf(NullPointerException.class);
    }

    @AllArgsConstructor
    @Getter
    public static class Price {

        private final String currency;
        private final BigDecimal amount;
    }

    @Getter
    public static class ItemView {

        private final long id;
        private final String name;
        private final Price listPrice;

        public ItemView(long id, String name, Price listPrice) {
            this.id = id;
            this.name = name;
            this.listPrice = listPrice;
        }
    }

    @AutoParameterizedTest
    void setParameterNameResolver_correctly_configures_resolver(
        ItemView source
    ) {
        // Arrange
        ParameterNameResolver resolver = p -> {
            if (p.getType().equals(long.class)) {
                return Optional.of("id");
            } else if (p.getType().equals(String.class)) {
                return Optional.of("name");
            } else if (p.getType().equals(Price.class)) {
                return Optional.of("listPrice");
            } else {
                return Optional.empty();
            }
        };

        Mapper sut = new Mapper(c -> c.setParameterNameResolver(resolver));

        // Act
        ItemView actual = sut.convert(source, ItemView.class);

        // Assert
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }

    @Test
    void apply_has_null_guard_for_configurer() {
        assertThatThrownBy(() -> new Mapper(config -> config.apply(null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("configurer");
    }

    @Test
    void apply_is_fluent() {
        new Mapper(config -> {
            MapperConfiguration actual = config.apply(m -> { });
            assertThat(actual).isSameAs(config);
        });
    }

    @AutoParameterizedTest
    void apply_correctly_configures_mapper(Pricing pricing) {
        // Arrange
        Consumer<MapperConfiguration> configurer = config -> config.map(
            Pricing.class,
            PricingView.class,
            mapping -> mapping.compute(
                "salePrice",
                (context, source) -> source.calculateSalePrice()
            )
        );

        Mapper mapper = new Mapper(config -> config.apply(configurer));

        // Act
        PricingView actual = mapper.convert(pricing, PricingView.class);

        // Assert
        assertThat(actual.getSalePrice())
            .isEqualTo(pricing.calculateSalePrice());
    }
}
