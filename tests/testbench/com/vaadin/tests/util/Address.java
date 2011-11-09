package com.vaadin.tests.util;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Address implements Serializable {

    private String streetAddress = "";
    private Integer postalCode = null;
    private String city = "";

    public Address() {
    }

    public Address(String streetAddress, int postalCode, String city) {
        setStreetAddress(streetAddress);
        setPostalCode(postalCode);
        setCity(city);
    }

    /**
     * @return the streetAddress
     */
    public String getStreetAddress() {
        return streetAddress;
    }

    /**
     * @param streetAddress
     *            the streetAddress to set
     */
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    /**
     * @return the postalCode
     */
    public Integer getPostalCode() {
        return postalCode;
    }

    /**
     * @param postalCode
     *            the postalCode to set
     */
    public void setPostalCode(Integer postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city
     *            the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

}
