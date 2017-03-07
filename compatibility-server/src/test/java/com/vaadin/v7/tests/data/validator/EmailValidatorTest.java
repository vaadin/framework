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

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.v7.data.validator.EmailValidator;

public class EmailValidatorTest {

    private EmailValidator validator = new EmailValidator("Error");

    @Test
    public void testEmailValidatorWithNull() {
        Assert.assertTrue(validator.isValid(null));
    }

    @Test
    public void testEmailValidatorWithEmptyString() {
        Assert.assertTrue(validator.isValid(""));
    }

    @Test
    public void testEmailValidatorWithFaultyString() {
        Assert.assertFalse(validator.isValid("not.an.email"));
    }

    @Test
    public void testEmailValidatorWithOkEmail() {
        Assert.assertTrue(validator.isValid("my.name@email.com"));
    }
}
