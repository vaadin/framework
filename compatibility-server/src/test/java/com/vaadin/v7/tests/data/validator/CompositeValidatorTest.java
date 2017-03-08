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
package com.vaadin.v7.tests.data.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.validator.CompositeValidator;
import com.vaadin.v7.data.validator.CompositeValidator.CombinationMode;
import com.vaadin.v7.data.validator.EmailValidator;
import com.vaadin.v7.data.validator.RegexpValidator;

public class CompositeValidatorTest {

    CompositeValidator and = new CompositeValidator(CombinationMode.AND,
            "One validator not valid");
    CompositeValidator or = new CompositeValidator(CombinationMode.OR,
            "No validators are valid");
    EmailValidator email = new EmailValidator("Faulty email");
    RegexpValidator regex = new RegexpValidator("@mail.com", false,
            "Partial match validator error");

    @Before
    public void setUp() {
        and.addValidator(email);
        and.addValidator(regex);

        or.addValidator(email);
        or.addValidator(regex);
    }

    @Test
    public void testCorrectValue() {
        String testString = "user@mail.com";
        assertTrue(email.isValid(testString));
        assertTrue(regex.isValid(testString));
        try {
            // notNull.validate(null);
            // fail("expected null to fail with an exception");
            and.validate(testString);
        } catch (Validator.InvalidValueException ex) {
            // assertEquals("Null not accepted", ex.getMessage());
            fail("And validator should be valid");
        }
        try {
            or.validate(testString);
        } catch (Validator.InvalidValueException ex) {
            // assertEquals("Null not accepted", ex.getMessage());
            fail("And validator should be valid");
        }
    }

    @Test
    public void testCorrectRegex() {

        String testString = "@mail.com";
        assertFalse(testString + " should not validate",
                email.isValid(testString));
        assertTrue(testString + "should validate", regex.isValid(testString));
        try {
            // notNull.validate(null);
            and.validate(testString);
            fail("expected and to fail with an exception");
        } catch (Validator.InvalidValueException ex) {
            assertEquals("Faulty email", ex.getMessage());
            // fail("And validator should be valid");
        }
        try {
            or.validate(testString);
        } catch (Validator.InvalidValueException ex) {
            // assertEquals("Null not accepted", ex.getMessage());
            fail("Or validator should be valid");
        }
    }

    @Test
    public void testCorrectEmail() {

        String testString = "user@gmail.com";

        assertTrue(testString + " should validate", email.isValid(testString));
        assertFalse(testString + " should not validate",
                regex.isValid(testString));
        try {
            and.validate(testString);
            fail("expected and to fail with an exception");
        } catch (Validator.InvalidValueException ex) {
            assertEquals("Partial match validator error", ex.getMessage());
        }
        try {
            or.validate(testString);
        } catch (Validator.InvalidValueException ex) {
            fail("Or validator should be valid");
        }
    }

    @Test
    public void testBothFaulty() {

        String testString = "gmail.com";

        assertFalse(testString + " should not validate",
                email.isValid(testString));
        assertFalse(testString + " should not validate",
                regex.isValid(testString));
        try {
            and.validate(testString);
            fail("expected and to fail with an exception");
        } catch (Validator.InvalidValueException ex) {
            assertEquals("Faulty email", ex.getMessage());
        }
        try {
            or.validate(testString);
            fail("expected or to fail with an exception");
        } catch (Validator.InvalidValueException ex) {
            assertEquals("No validators are valid", ex.getMessage());
        }
    }

}
