/*
 * Copyright 2000-2016 Vaadin Ltd.
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
