package com.vaadin.tests.components.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
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
        WebElement textArea = $(TextAreaElement.class).first();
        Actions builder = new Actions(driver);
        builder.click(textArea);
        builder.sendKeys(textArea, "first line asdf");
        builder.perform();
        waitUntilLoadingIndicatorNotVisible();

        builder.sendKeys(Keys.ENTER);
        builder.perform();
        waitUntilLoadingIndicatorNotVisible();

        builder.sendKeys(textArea, "second line jkl;");
        builder.perform();
        waitUntilLoadingIndicatorNotVisible();

        builder.perform();

        // Should not have triggered shortcut
        assertEquals(" ", getLogRow(0));
    }

    @Test
    public void testTextAreaEscapeEventPropagation()
            throws InterruptedException {
        openTestURL();
        WebElement textArea = $(TextAreaElement.class).first();
        Actions builder = new Actions(driver);
        builder.click(textArea);
        builder.sendKeys(textArea, "first line asdf");
        builder.perform();
        waitUntilLoadingIndicatorNotVisible();

        builder.sendKeys(Keys.ESCAPE);
        builder.perform();
        waitUntilLoadingIndicatorNotVisible();

        builder.sendKeys(textArea, "second line jkl;");
        builder.perform();
        waitUntilLoadingIndicatorNotVisible();

        assertEquals("1. Escape button pressed", getLogRow(0));
    }

    @Test
    public void testTextFieldEscapeEventPropagation() {
        openTestURL();
        WebElement textField = $(TextFieldElement.class).first();
        Actions builder2 = new Actions(driver);
        builder2.click(textField);

        builder2.sendKeys("third line");
        builder2.perform();
        waitUntilLoadingIndicatorNotVisible();

        builder2.sendKeys(Keys.ENTER);
        builder2.perform();
        waitUntilLoadingIndicatorNotVisible();

        builder2.sendKeys(Keys.ESCAPE);
        builder2.perform();
        waitUntilLoadingIndicatorNotVisible();

        assertEquals("1. Enter button pressed", getLogRow(1));
        assertEquals("2. Escape button pressed", getLogRow(0));
    }

    @Test
    public void testTextFieldEnterEventPropagation() {
        openTestURL();
        WebElement textField = $(TextFieldElement.class).first();
        Actions builder2 = new Actions(driver);
        builder2.click(textField);

        builder2.sendKeys("third line");
        builder2.perform();
        waitUntilLoadingIndicatorNotVisible();

        builder2.sendKeys(Keys.ENTER);
        builder2.perform();
        waitUntilLoadingIndicatorNotVisible();

        assertEquals("1. Enter button pressed", getLogRow(0));
    }
}
