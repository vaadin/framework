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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.v7.data.validator.RegexpValidator;

public class RegexpValidatorTest {

    private RegexpValidator completeValidator = new RegexpValidator("pattern",
            true, "Complete match validator error");
    private RegexpValidator partialValidator = new RegexpValidator("pattern",
            false, "Partial match validator error");

    @Test
    public void testRegexpValidatorWithNull() {
        assertTrue(completeValidator.isValid(null));
        assertTrue(partialValidator.isValid(null));
    }

    @Test
    public void testRegexpValidatorWithEmptyString() {
        assertTrue(completeValidator.isValid(""));
        assertTrue(partialValidator.isValid(""));
    }

    @Test
    public void testCompleteRegexpValidatorWithFaultyString() {
        assertFalse(completeValidator.isValid("mismatch"));
        assertFalse(completeValidator.isValid("pattern2"));
        assertFalse(completeValidator.isValid("1pattern"));
    }

    @Test
    public void testCompleteRegexpValidatorWithOkString() {
        assertTrue(completeValidator.isValid("pattern"));
    }

    @Test
    public void testPartialRegexpValidatorWithFaultyString() {
        assertFalse(partialValidator.isValid("mismatch"));
    }

    @Test
    public void testPartialRegexpValidatorWithOkString() {
        assertTrue(partialValidator.isValid("pattern"));
        assertTrue(partialValidator.isValid("1pattern"));
        assertTrue(partialValidator.isValid("pattern2"));
        assertTrue(partialValidator.isValid("1pattern2"));
    }
}
