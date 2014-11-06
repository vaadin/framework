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
package com.vaadin.tests.components.textfield;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class AlternatingTextFieldsTest extends MultiBrowserTest {

    private String RANDOM_INPUT = "Some input here";

    private List<TextFieldElement> textfields;

    @Test
    public void testInputPrompt() {
        openTestURL();

        /*
         * Starting positions
         */

        createTextFields();

        // test that both input prompts exist in the beginning
        ensureTextFieldHasInputPrompt(0);
        ensureTextFieldHasInputPrompt(1);

        /*
         * Write on and empty the first TextField
         */

        // select first input prompt
        ensureSelectionClearsPrompt(0);

        // write on the first TextField
        ensureWritingDisablesOther(0);

        // empty the text on the first TextField
        ensureEmptyingAddsPromptAndEnablesOther(0);

        /*
         * Write on and empty the second TextField
         */

        // now select the second input prompt
        ensureSelectionClearsPrompt(1);

        // write on the second TextField
        ensureWritingDisablesOther(1);

        // empty the text on the second TextField
        ensureEmptyingAddsPromptAndEnablesOther(1);

    }

    private void ensureEmptyingAddsPromptAndEnablesOther(int index) {
        // remove the text from the TextField
        emptyTextField(index);

        // ensure that the TextField really is empty
        ensureTextFieldEmpty(index);

        // ensure that the other TextField has again been enabled and has an
        // input prompt
        if (index == 0) {
            ensureTextFieldIsEnabledAndHasInputPrompt(1);
        } else {
            ensureTextFieldIsEnabledAndHasInputPrompt(0);
        }
    }

    private void ensureWritingDisablesOther(int index) {
        // write some text to the TextField
        writeOnTextField(index);

        // ensure that the other TextField is disabled and has no input prompt
        if (index == 0) {
            ensureTextFieldDisabledAndEmpty(1);
        } else {
            ensureTextFieldDisabledAndEmpty(0);
        }
    }

    private void ensureSelectionClearsPrompt(int index) {
        // select the TextField
        textfields.get(index).click();

        // check that the the prompt was removed
        ensureTextFieldEmpty(index);
    }

    /**
     * Check that the TextField has no input prompt
     * 
     * @since
     * @param index
     *            The TextField to be inspected
     */
    private void ensureTextFieldEmpty(int index) {

        assertEquals("TextField " + index + " was not empty,", "", textfields
                .get(index).getValue());
    }

    /**
     * Check that the TextField has been enabled and has correct input prompt
     * 
     * @since
     * @param index
     *            the TextField to be inspected
     */
    private void ensureTextFieldIsEnabledAndHasInputPrompt(final int index) {

        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return textfields.get(index).isEnabled();
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return "TextField " + index + " to be enabled";
            }
        });

        ensureTextFieldHasInputPrompt(index);
    }

    /**
     * Check that the TextField has the correct input prompt
     * 
     * @since
     * @param index
     *            The TextField to be inspected
     */
    private void ensureTextFieldHasInputPrompt(final int index) {

        if (index == 0) {
            assertEquals("Incorrect or missing prompt,",
                    AlternatingTextFields.FIRST_TEXTFIELD_INPUT_PROMPT,
                    textfields.get(index).getValue());
        } else {
            assertEquals("Incorrect or missing prompt,",
                    AlternatingTextFields.SECOND_TEXTFIELD_INPUT_PROMPT,
                    textfields.get(index).getValue());
        }
    }

    /**
     * Check that the TextField has been disabled and has no input prompt
     * 
     * @since
     * @param index
     *            The TextField to be inspected
     */
    private void ensureTextFieldDisabledAndEmpty(final int index) {

        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return !textfields.get(index).isEnabled();
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return "TextField " + index + " to be disabled";
            }
        });

        ensureTextFieldEmpty(index);
    }

    private void createTextFields() {
        textfields = $(TextFieldElement.class).all();
    }

    private void writeOnTextField(int index) {
        textfields.get(index).sendKeys(RANDOM_INPUT);
    }

    private void emptyTextField(int index) {
        for (int i = 0; i < 15; i++) {
            textfields.get(index).sendKeys(Keys.BACK_SPACE);
        }
    }
}
