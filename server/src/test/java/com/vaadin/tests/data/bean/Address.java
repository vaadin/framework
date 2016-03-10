package com.vaadin.tests.data.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Address implements Serializable {

    private String streetAddress = "";
    private Integer postalCode = null;
    private String city = "";
    private Country country = null;

    public Address() {

    }

    public Address(String streetAddress, int postalCode, String city,
            Country country) {
        setStreetAddress(streetAddress);
        setPostalCode(postalCode);
        setCity(city);
        setCountry(country);
    }

    @Override
    public String toString() {
        return "Address [streetAddress=" + streetAddress + ", postalCode="
                + postalCode + ", city=" + city + ", country=" + country + "]";
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public Integer getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(Integer postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

}
