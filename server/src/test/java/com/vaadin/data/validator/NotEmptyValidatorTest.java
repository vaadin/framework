/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.data.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.ValueContext;

/**
 * @author Vaadin Ltd
 *
 */
public class NotEmptyValidatorTest {

    @Test
    public void nullValueIsDisallowed() {
        NotEmptyValidator<String> validator = new NotEmptyValidator<>("foo");
        ValidationResult result = validator.apply(null, new ValueContext());
        assertTrue(result.isError());
        assertEquals("foo", result.getErrorMessage());
    }

    @Test
    public void emptyValueIsDisallowed() {
        NotEmptyValidator<String> validator = new NotEmptyValidator<>("foo");
        ValidationResult result = validator.apply("", new ValueContext());
        assertTrue(result.isError());
        assertEquals("foo", result.getErrorMessage());
    }

    @Test
    public void nonNullValueIsAllowed() {
        NotEmptyValidator<Object> validator = new NotEmptyValidator<>("foo");
        Object value = new Object();
        ValidationResult result = validator.apply(value, new ValueContext());
        assertFalse(result.isError());
        assertFalse(result.isError());
    }
}
