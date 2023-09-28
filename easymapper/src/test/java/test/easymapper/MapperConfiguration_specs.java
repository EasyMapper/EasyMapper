package test.easymapper;

import easymapper.ConstructorExtractor;
import easymapper.Mapper;
import easymapper.MapperConfiguration;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

import static easymapper.MapperConfiguration.configureMapper;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MapperConfiguration_specs {

    @Test
    void configureMapper_returns_mapper_configuration() {
        MapperConfiguration actual = configureMapper(builder -> { });
        assertThat(actual).isNotNull();
    }

    @AutoParameterizedTest
    void sut_correctly_configures_constructor_property_mapping(Order source) {
        // Arrange
        Mapper mapper = new Mapper(configureMapper(config -> config
            .addMapping(Order.class, OrderView.class, mapping -> mapping
                .map("quantity", "numberOfItems"))));

        // Act
        OrderView actual = mapper.map(source, OrderView.class);

        // Assert
        assertThat(actual.getNumberOfItems()).isEqualTo(source.getQuantity());
    }

    @AutoParameterizedTest
    void sut_correctly_configures_settable_property_mapping(Recipient source) {
        // Arrange
        Mapper mapper = new Mapper(configureMapper(config -> config
            .addMapping(Recipient.class, RecipientView.class, mapping -> mapping
                .map("name", "recipientName")
                .map("phoneNumber", "recipientPhoneNumber"))));

        // Act
        RecipientView actual = mapper.map(source, RecipientView.class);

        // Assert
        assertThat(actual.getRecipientName()).isEqualTo(source.getName());
        assertThat(actual.getRecipientPhoneNumber()).isEqualTo(source.getPhoneNumber());
    }

    @AutoParameterizedTest
    void sut_does_not_allow_duplicate_source_property_name(String destinationPropertyName) {
        assertThatThrownBy(() ->
            configureMapper(config -> config
                .addMapping(Order.class, OrderView.class, mapping -> mapping
                    .map("quantity", "numberOfItems")
                    .map("quantity", destinationPropertyName))));
    }

    @AutoParameterizedTest
    void sut_does_not_allow_duplicate_destination_property_name(String sourcePropertyName) {
        assertThatThrownBy(() ->
            configureMapper(config -> config
                .addMapping(Order.class, OrderView.class, mapping -> mapping
                    .map("quantity", "numberOfItems")
                    .map(sourcePropertyName, "numberOfItems"))));
    }

    @Test
    void sut_has_guard_against_null_mapper_configurer() {
        assertThatThrownBy(() -> configureMapper(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void sut_has_guard_against_null_source_type() {
        assertThatThrownBy(() ->
            configureMapper(config -> config
                .addMapping(null, OrderView.class, mapping -> { })));
    }

    @Test
    void sut_has_guard_against_null_destination_type() {
        assertThatThrownBy(() ->
            configureMapper(config -> config
                .addMapping(Order.class, null, mapping -> { })));
    }

    @Test
    void sut_has_guard_against_null_mapping_configurer() {
        assertThatThrownBy(() ->
            configureMapper(config -> config
                .addMapping(Order.class, OrderView.class, null)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void sut_has_guard_against_source_property_name() {
        assertThatThrownBy(() ->
            configureMapper(config -> config
                .addMapping(Order.class, OrderView.class, mapping -> mapping
                    .map(null, "numberOfItems"))));
    }

    @Test
    void sut_has_guard_against_destination_property_name() {
        assertThatThrownBy(() ->
            configureMapper(config -> config
                .addMapping(Order.class, OrderView.class, mapping -> mapping
                    .map("quantity", null))));
    }

    @AutoParameterizedTest
    void constructor_extractor_correctly_works(User source) {
        // Arrange
        ConstructorExtractor extractor = type -> stream(type.getConstructors())
            .sorted(comparingInt(Constructor::getParameterCount))
            .limit(1)
            .collect(toList());
        Mapper mapper = new Mapper(configureMapper(c -> c.setConstructorExtractor(extractor)));

        // Act
        HasBrokenConstructor actual = mapper.map(source, HasBrokenConstructor.class);

        // Assert
        assertThat(actual.getId()).isEqualTo(source.getId());
        assertThat(actual.getUsername()).isEqualTo(HasBrokenConstructor.DEFAULT_USERNAME);
    }

    @AutoParameterizedTest
    void setConstructorExtractor_is_fluent() {
        new Mapper(configureMapper(c -> assertThat(
            c.setConstructorExtractor(c.getConstructorExtractor())).isSameAs(c)));
    }

    @AutoParameterizedTest
    void setConstructorExtractor_has_guard_against_null_value() {
        assertThatThrownBy(() -> configureMapper(c -> c.setConstructorExtractor(null)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addMapping_is_fluent() {
        new Mapper(configureMapper(c -> assertThat(
            c.addMapping(Order.class, OrderView.class, m -> { })).isSameAs(c)));
    }
}
