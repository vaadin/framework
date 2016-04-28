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
package com.vaadin.tests.fieldgroup;

import java.util.Random;

import com.vaadin.tests.util.TestDataGenerator;

public class ComplexAddress {

    private String streetAddress = "";
    private String postalCode = "";
    private String city = "";
    private Country country = null;

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
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

    public static ComplexAddress create(Random r) {
        ComplexAddress ca = new ComplexAddress();
        ca.setCity(TestDataGenerator.getCity(r));
        ca.setCountry(TestDataGenerator.getEnum(Country.class, r));
        ca.setPostalCode(TestDataGenerator.getPostalCode(r) + "");
        ca.setStreetAddress(TestDataGenerator.getStreetAddress(r));
        return ca;
    }

}
