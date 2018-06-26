package com.vaadin.tests.components.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TextAreaEventPropagationModifierKeysTest extends MultiBrowserTest {
    @Test
    public void textAreaShiftEnterEventPropagation()
            throws InterruptedException {
        openTestURL();

        WebElement textArea = $(TextAreaElement.class).first();
        Actions builder = new Actions(driver);
        builder.click(textArea);
        builder.sendKeys(textArea, "first line asdf");
        builder.sendKeys(Keys.chord(Keys.SHIFT, Keys.ENTER));
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
        builder.sendKeys(Keys.chord(Keys.CONTROL, Keys.ENTER));
        builder.sendKeys(textArea, "second line jkl;");
        builder.perform();

        // Should have triggered shortcut
        assertEquals("1. Ctrl-Enter button pressed", getLogRow(0));
    }

    @Override
    protected Class<?> getUIClass() {
        return TextAreaEventPropagation.class;
    }
}
