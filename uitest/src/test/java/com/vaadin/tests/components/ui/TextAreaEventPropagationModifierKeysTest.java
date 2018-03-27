package com.vaadin.tests.components.ui;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.parallel.Browser;
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
        builder.keyDown(Keys.SHIFT);
        builder.sendKeys(Keys.ENTER);
        builder.keyUp(Keys.SHIFT);
        builder.sendKeys(textArea, "second line jkl;");
        builder.perform();

        // Should have triggered shortcut
        Assert.assertEquals("1. Shift-Enter button pressed", getLogRow(0));
    }

    @Test
    public void textAreaCtrlEnterEventPropagation()
            throws InterruptedException {
        openTestURL();

        WebElement textArea = $(TextAreaElement.class).first();
        Actions builder = new Actions(driver);
        builder.click(textArea);
        builder.sendKeys(textArea, "first line asdf");
        builder.keyDown(Keys.CONTROL);
        builder.sendKeys(Keys.ENTER);
        builder.keyUp(Keys.CONTROL);
        builder.sendKeys(textArea, "second line jkl;");
        builder.perform();

        // Should have triggered shortcut
        Assert.assertEquals("1. Ctrl-Enter button pressed", getLogRow(0));
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // IE8 and Firefox can't handle ctrl.
        // IE9-11 has issues with shift and ctrl
        return getBrowserCapabilities(Browser.CHROME, Browser.PHANTOMJS);
    }

    @Override
    protected Class<?> getUIClass() {
        return TextAreaEventPropagation.class;
    }
}
