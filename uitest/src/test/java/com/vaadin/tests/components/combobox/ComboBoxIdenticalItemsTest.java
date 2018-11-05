package com.vaadin.tests.components.combobox;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for identical item captions in ComboBox.
 *
 * @author Vaadin Ltd
 */
public class ComboBoxIdenticalItemsTest extends MultiBrowserTest {

    @Test
    public void identicalItemsKeyboardTest() {
        openTestURL();
        int delay = BrowserUtil.isPhantomJS(getDesiredCapabilities()) ? 500 : 0;

        ComboBoxElement combobox = $(ComboBoxElement.class).first();

        combobox.sendKeys(delay, Keys.ARROW_DOWN, getReturn());
        waitUntilLogText("1. Item one-1 selected");

        Keys[] downDownEnter = { Keys.ARROW_DOWN, Keys.ARROW_DOWN,
                getReturn() };

        combobox.sendKeys(delay, downDownEnter);
        waitUntilLogText("2. Item one-2 selected");

        combobox.sendKeys(delay, downDownEnter);
        waitUntilLogText("3. Item two selected");

        combobox.sendKeys(delay, new Keys[] { Keys.ARROW_UP, Keys.ARROW_UP,
                Keys.ARROW_UP, getReturn() });
        waitUntilLogText("4. Item one-1 selected");
    }

    private Keys getReturn() {
        if (BrowserUtil.isPhantomJS(getDesiredCapabilities())) {
            return Keys.ENTER;
        }
        return Keys.RETURN;
    }

    private void waitUntilLogText(final String expected) {
        waitUntil(new ExpectedCondition<Boolean>() {
            private String text;

            @Override
            public Boolean apply(WebDriver input) {
                text = findElement(By.vaadin("PID_SLog_row_0")).getText();
                return text.equals(expected);
            }

            @Override
            public String toString() {
                return String.format(
                        "log content to update. Expected: '%s' (was: '%s')",
                        expected, text);
            }
        });
    }
}
