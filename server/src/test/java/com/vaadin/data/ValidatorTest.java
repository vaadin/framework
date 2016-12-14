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
package com.vaadin.data;

import java.util.Locale;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.validator.ValidatorTestBase;

/**
 * @author Vaadin Ltd
 *
 */
public class ValidatorTest extends ValidatorTestBase {

    @Test
    public void alwaysPass() {
        Validator<String> alwaysPass = Validator.alwaysPass();
        ValidationResult result = alwaysPass.apply("foo", new ValueContext());
        Assert.assertFalse(result.isError());
    }

    @Test
    public void from() {
        Validator<String> validator = Validator.from(Objects::nonNull,
                "Cannot be null");
        ValidationResult result = validator.apply(null, new ValueContext());
        Assert.assertTrue(result.isError());

        result = validator.apply("", new ValueContext());
        Assert.assertFalse(result.isError());
    }

    @Test
    public void withValidator_customErrorMessageProvider() {
        String finnishError = "Käyttäjän tulee olla täysi-ikäinen";
        String englishError = "The user must be an adult";
        String notTranslatableError = "NOT TRANSLATABLE";

        Validator<Integer> ageValidator = Validator.from(age -> age >= 18,
                ctx -> {
                    Locale locale = ctx.getLocale().orElse(Locale.ENGLISH);

                    if (locale.getLanguage().equals("fi")) {
                        return finnishError;
                    } else if (locale.getLanguage().equals("en")) {
                        return englishError;
                    }
                    return notTranslatableError;
                });

        setLocale(Locale.ENGLISH);
        assertFails(17, englishError, ageValidator);
        setLocale(new Locale("fi", "FI"));
        assertFails(17, finnishError, ageValidator);
        setLocale(Locale.GERMAN);
        assertFails(17, notTranslatableError, ageValidator);
    }
}
