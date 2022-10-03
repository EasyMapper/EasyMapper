package cleanpojo.japper.test;

import java.beans.ConstructorProperties;

public final class Recipient {

    private String name;
    private String phoneNumber;

    @ConstructorProperties({ "name", "phoneNumber" })
    public Recipient(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
