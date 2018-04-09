package com.vaadin.tests.components.textfield;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TextFieldEmptyingPromptTest extends MultiBrowserTest {

    private String RANDOM_INPUT = "Some input here";

    private TextFieldElement textfield;
    private LabelElement label;
    private ButtonElement button;

    @Test
    public void testInputPrompt() throws InterruptedException {
        openTestURL();

        textfield = $(TextFieldElement.class).first();
        label = $(LabelElement.class).get(1);
        button = $(ButtonElement.class).first();

        // Write on the TextField
        writeOnTextField();

        // Make sure a complete server communication cycle happened
        waitServerUpdate("Textfield value: " + RANDOM_INPUT);

        // Empty the TextField
        emptyTextField();

        // Click attempts to remove the prompt
        button.click();

        // Assert Prompt text disappeared
        waitServerUpdateText("");
    }

    private void waitServerUpdate(final String expectedValue) {
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return label.getText().equals(expectedValue);
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return "the server to get updated with the entered value: '"
                        + expectedValue + "' (was: '" + label.getText() + "')";
            }
        });
    }

    private void waitServerUpdateText(final String expectedValue) {
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return textfield.getValue().equals(expectedValue);
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return "the server to get updated with the entered value: '"
                        + expectedValue + "' (was: '" + textfield.getValue()
                        + "')";
            }
        });
    }

    private void writeOnTextField() {
        textfield.sendKeys(RANDOM_INPUT);
    }

    private void emptyTextField() {
        for (int i = 0; i < RANDOM_INPUT.length(); i++) {
            textfield.sendKeys(Keys.BACK_SPACE);
        }
    }
}
