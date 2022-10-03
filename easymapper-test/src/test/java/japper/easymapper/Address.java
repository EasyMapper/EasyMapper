package japper.easymapper;

import java.beans.ConstructorProperties;

public class Address {

    private final String country;
    private final String state;
    private final String city;
    private final String zipCode;

    @ConstructorProperties({ "country", "state", "city", "zipCode" })
    public Address(String country, String state, String city, String zipCode) {
        this.country = country;
        this.state = state;
        this.city = city;
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getZipCode() {
        return zipCode;
    }
}
