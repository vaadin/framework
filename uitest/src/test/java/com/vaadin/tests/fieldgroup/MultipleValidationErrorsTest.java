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
package com.vaadin.tests.fieldgroup;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class MultipleValidationErrorsTest extends MultiBrowserTest {

    private void commitTextFields() {
        $(ButtonElement.class).caption("Submit").first().click();
    }

    private void clearTextField(String caption) {
        TextFieldElement textField = $(TextFieldElement.class).caption(caption)
                .first();
        textField.clear();
    }

    @Test
    public void validationErrorsIncludeBothErrors() {
        openTestURL();

        clearTextField("First Name");
        clearTextField("Last Name");

        commitTextFields();

        String validationErrors = $(LabelElement.class).id("validationErrors")
                .getText();

        assertThat(validationErrors, containsString(
                MultipleValidationErrors.FIRST_NAME_NOT_EMPTY_VALIDATION_MESSAGE));
        assertThat(validationErrors, containsString(
                MultipleValidationErrors.LAST_NAME_NOT_EMPTY_VALIDATION_MESSAGE));
    }
}
