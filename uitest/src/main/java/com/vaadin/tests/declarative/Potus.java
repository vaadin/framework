package com.vaadin.tests.declarative;

import java.util.Date;

/**
 *
 * @since
 * @author Vaadin Ltd
 */
public class Potus {
    private String firstName;
    private String lastName;
    private String party;
    private Date tookOffice;
    private Date leftOffice;

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
     * @return the party
     */
    public String getParty() {
        return party;
    }

    /**
     * @param party
     *            the party to set
     */
    public void setParty(String party) {
        this.party = party;
    }

    /**
     * @return the tookOffice
     */
    public Date getTookOffice() {
        return tookOffice;
    }

    /**
     * @param tookOffice
     *            the tookOffice to set
     */
    public void setTookOffice(Date tookOffice) {
        this.tookOffice = tookOffice;
    }

    /**
     * @return the leftOffice
     */
    public Date getLeftOffice() {
        return leftOffice;
    }

    /**
     * @param leftOffice
     *            the leftOffice to set
     */
    public void setLeftOffice(Date leftOffice) {
        this.leftOffice = leftOffice;
    }

}
