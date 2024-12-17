package test.easymapper;

import easymapper.Mapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static org.assertj.core.api.Assertions.assertThat;

public class Record_specs {

    @AllArgsConstructor
    @Getter
    public static class User {
        private long id;
        private String username;
        private String passwordHash;
    }

    public record UserView(long id, String username) {
    }

    @AutoParameterizedTest
    void sut_correctly_maps_to_record(Mapper sut, User source) {
        var actual = sut.map(source, User.class, UserView.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(source);
    }

    public record Recipient(String name, String phoneNumber) {
    }

    @Getter
    @Setter
    public static class RecipientView {
        private String recipientName;
        private String recipientPhoneNumber;
    }

    @AutoParameterizedTest
    void sut_correctly_maps_from_record(Recipient recipient) {
        var sut = new Mapper(config -> config
            .map(Recipient.class, RecipientView.class, mapping -> mapping
                .compute("recipientName", context -> Recipient::name)
                .compute("recipientPhoneNumber", context -> Recipient::phoneNumber)));

        var actual = sut.map(recipient, Recipient.class, RecipientView.class);

        assertThat(actual.getRecipientName()).isEqualTo(recipient.name());
        assertThat(actual.getRecipientPhoneNumber()).isEqualTo(recipient.phoneNumber());
    }
}
