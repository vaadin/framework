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
package com.vaadin.data.validator;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.ui.Label;

public class ValidatorTestBase {

    private Label localeContext;

    @Before
    public void setUp() {
        localeContext = new Label();
        setLocale(Locale.US);
    }

    protected <T> void assertPasses(T value, Validator<? super T> validator) {
        ValidationResult result = validator.apply(value, new ValueContext());
        if (result.isError()) {
            Assert.fail(value + " should pass " + validator + " but got "
                    + result.getErrorMessage());
        }
    }

    protected <T> void assertFails(T value, String errorMessage,
            Validator<? super T> validator) {
        ValidationResult result = validator.apply(value,
                new ValueContext(localeContext));
        Assert.assertTrue(result.isError());
        Assert.assertEquals(errorMessage, result.getErrorMessage());
    }

    protected <T> void assertFails(T value, AbstractValidator<? super T> v) {
        assertFails(value, v.getMessage(value), v);
    }

    protected void setLocale(Locale locale) {
        localeContext.setLocale(locale);
    }
}
