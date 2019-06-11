package com.vaadin.tests.components.ui;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

import static org.junit.Assert.assertEquals;

public class TextAreaEventPropagationModifierKeysTest extends SingleBrowserTest {

    @Test
    public void textAreaShiftEnterEventPropagation()
            throws InterruptedException {
        openTestURL();
        Actions builder = new Actions(driver);
        WebElement textArea = $(TextAreaElement.class).first();
        builder.click(textArea);
        builder.sendKeys(textArea, "first line asdf");
        pressKeyCombinations(Keys.SHIFT, Keys.ENTER);
        builder.sendKeys(textArea, "second line jkl;");
        builder.perform();

        // Should have triggered shortcut
        assertEquals("1. Shift-Enter button pressed", getLogRow(0));
    }

    @Test
    public void textAreaCtrlEnterEventPropagation()
            throws InterruptedException {
        openTestURL();

        WebElement textArea = $(TextAreaElement.class).first();
        Actions builder = new Actions(driver);
        builder.click(textArea);
        builder.sendKeys(textArea, "first line asdf");
        pressKeyCombinations(Keys.CONTROL, Keys.ENTER);
        builder.sendKeys(textArea, "second line jkl;");
        builder.perform();

        // Should have triggered shortcut
        assertEquals("1. Ctrl-Enter button pressed", getLogRow(0));
    }

    @Override
    protected Class<?> getUIClass() {
        return TextAreaEventPropagation.class;
    }

    // That is a workaround after Chrome 75, sendKeys(Keys.shift, Keys.Tab) doesn't work
    protected void pressKeyCombinations(Keys keyModifier, Keys key){

        Actions builder = new Actions(driver);
        builder.keyDown(keyModifier).perform();
        builder.sendKeys(Keys.chord(key)).perform();
        builder.keyUp(keyModifier).perform();
    }
}
