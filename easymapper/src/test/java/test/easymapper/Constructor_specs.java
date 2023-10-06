package test.easymapper;

import easymapper.Mapper;
import test.easymapper.fixture.ItemView;
import test.easymapper.fixture.User;
import test.easymapper.fixture.UserEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class Constructor_specs {

    @AutoParameterizedTest
    void sut_chooses_constructor_with_most_parameters(Mapper sut, User source) {
        UserEntity actual = sut.map(source, User.class, UserEntity.class);
        assertThat(actual.getId()).isEqualTo(source.getId());
    }

    @AutoParameterizedTest
    void sut_fails_with_useful_message_if_constructor_not_decorated_with_constructor_properties_annotation(
        Mapper sut,
        ItemView source
    ) {
        assertThatThrownBy(() -> sut.map(source, ItemView.class, ItemView.class))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContainingAll("ItemView", "@ConstructorProperties");
    }
}
