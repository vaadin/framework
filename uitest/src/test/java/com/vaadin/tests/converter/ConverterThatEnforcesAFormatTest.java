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
package com.vaadin.tests.converter;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ConverterThatEnforcesAFormatTest extends MultiBrowserTest {

    private TextFieldElement field;

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        field = $(TextFieldElement.class).first();
    }

    @Test
    public void checkDefault() {
        waitUntilValueIs("50.000");
    }

    @Test
    public void checkRounding() {
        setValue("50.0202", Keys.ENTER);
        waitUntilValueIs("50.020");
    }

    @Test
    public void checkElaborating() {
        setValue("12");
        waitUntilValueIs("12.000");
    }

    @Test
    public void checkText() {
        setValue("abc", Keys.ENTER);
        waitUntilValueIs("abc");
        waitUntilHasCssClass("v-textfield-error");
    }

    private void setValue(String value, CharSequence... keysToSend) {
        field.setValue(value);
        if (keysToSend.length > 0) {
            field.sendKeys(keysToSend);
        } else {
            field.submit();
        }
    }

    private void waitUntilValueIs(final String expected) {
        waitUntil(new ExpectedCondition<Boolean>() {
            private String actual;

            @Override
            public Boolean apply(WebDriver arg0) {
                actual = field.getValue();
                return expected.equals(actual);
            }

            @Override
            public String toString() {
                return String.format("the field to have value '%s' (was: '%s')",
                        expected, actual);
            }
        });
    }

    private void waitUntilHasCssClass(final String className) {
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver arg0) {
                return hasCssClass(field, className);
            }

            @Override
            public String toString() {
                return String.format("the field to have css class '%s'",
                        className);
            }
        });
    }

}
