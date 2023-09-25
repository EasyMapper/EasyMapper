package test.easymapper;

import easymapper.Mapper;
import easymapper.MapperConfiguration;
import org.junit.jupiter.api.Test;

import static easymapper.MapperConfiguration.configureMapper;
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
}
