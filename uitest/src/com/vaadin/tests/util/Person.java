package com.vaadin.tests.util;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Person implements Serializable {
    private String firstName = "";
    private String lastName = "";
    private String email = "";
    private String phoneNumber = "";
    private Address address = new Address();

    public Person() {
        address = new Address();
    }

    public Person(String firstName, String lastName, String email,
            String phoneNumber, String streetAddress, int postalCode,
            String city) {
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setPhoneNumber(phoneNumber);
        address = new Address(streetAddress, postalCode, city);
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName
     *            the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName
     *            the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber
     *            the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns the address where the person lives.
     * 
     * @return address (not null)
     */
    public Address getAddress() {
        return address;
    }

}
