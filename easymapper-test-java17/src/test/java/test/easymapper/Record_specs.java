package test.easymapper;

import easymapper.Mapper;
import test.easymapper.fixture.Recipient;
import test.easymapper.fixture.RecipientView;
import test.easymapper.fixture.User;
import test.easymapper.fixture.UserView;

import static org.assertj.core.api.Assertions.assertThat;

public class Record_specs {

    @AutoParameterizedTest
    void sut_correctly_maps_to_record(Mapper sut, User source) {
        var actual = sut.map(source, User.class, UserView.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }

    @AutoParameterizedTest
    void sut_correctly_maps_from_record(Recipient source) {
        var sut = new Mapper(config -> config
            .addMapping(Recipient.class, RecipientView.class, mapping -> mapping
                .set("recipientName", Recipient::name)
                .set("recipientPhoneNumber", Recipient::phoneNumber)));

        var actual = sut.map(source, Recipient.class, RecipientView.class);

        assertThat(actual.getRecipientName()).isEqualTo(source.name());
        assertThat(actual.getRecipientPhoneNumber()).isEqualTo(source.phoneNumber());
    }
}
