/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.declarative;

import java.util.Date;

/**
 * 
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
