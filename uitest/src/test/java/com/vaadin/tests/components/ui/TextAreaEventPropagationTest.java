package com.vaadin.tests.components.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that the TextArea widget correctly stops ENTER events from propagating.
 *
 * @author Vaadin Ltd
 */
public class TextAreaEventPropagationTest extends MultiBrowserTest {

    @Test
    public void textAreaEnterEventPropagation() throws InterruptedException {
        openTestURL();
        TextAreaElement textArea = $(TextAreaElement.class).first();
        Actions builder = new Actions(driver);
        builder.click(textArea);
        builder.sendKeys(textArea, "first line asdf", Keys.ENTER,
                "second line jkl;");
        builder.perform();
        waitUntilLoadingIndicatorNotVisible();

        String text = textArea.getValue();
        assertEquals("Unexpected text area content,",
                "first line asdf\nsecond line jkl;", text);
        // Should not have triggered shortcut
        assertEquals(" ", getLogRow(0));
    }

    @Test
    public void testTextAreaEscapeEventPropagation()
            throws InterruptedException {
        openTestURL();
        TextAreaElement textArea = $(TextAreaElement.class).first();
        Actions builder = new Actions(driver);
        builder.click(textArea);
        builder.sendKeys(textArea, "first line asdf", Keys.ESCAPE,
                "second line jkl;");
        builder.perform();
        waitUntilLoadingIndicatorNotVisible();

        String text = textArea.getValue();
        // sendKeys is erratic and can eat some letters after escape, so only
        // test that beginning and end are present
        assertTrue("Unexpected text area content: " + text,
                text.startsWith("first line asdf"));
        assertTrue("Unexpected text area content: " + text,
                text.endsWith("nd line jkl;"));
        assertFalse("Unexpected text area content: " + text,
                text.contains("\n"));
        assertEquals("1. Escape button pressed", getLogRow(0));
    }

    @Test
    public void testTextFieldEscapeEventPropagation() {
        openTestURL();
        TextFieldElement textField = $(TextFieldElement.class).first();
        Actions builder2 = new Actions(driver);
        builder2.click(textField);

        builder2.sendKeys(textField, "third line", Keys.ESCAPE);
        builder2.perform();
        waitUntilLoadingIndicatorNotVisible();

        String text = textField.getValue();
        assertEquals("Unexpected text field content,", "third line", text);
        assertEquals("1. Escape button pressed", getLogRow(0));
    }

    @Test
    public void testTextFieldEnterEventPropagation() {
        openTestURL();
        TextFieldElement textField = $(TextFieldElement.class).first();
        Actions builder2 = new Actions(driver);
        builder2.click(textField);

        builder2.sendKeys(textField, "third line", Keys.ENTER);
        builder2.perform();
        waitUntilLoadingIndicatorNotVisible();

        String text = textField.getValue();
        assertEquals("Unexpected text field content,", "third line", text);
        assertEquals("1. Enter button pressed", getLogRow(0));
    }
}
