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
package com.vaadin.tests.components.window;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests close shortcuts for Window.
 * 
 * @author Vaadin Ltd
 */
public class CloseShortcutTest extends MultiBrowserTest {

    private WindowElement window;
    private CheckBoxElement cbDefault;
    private CheckBoxElement cbOther;
    private CheckBoxElement cbCtrl;
    private CheckBoxElement cbShift;

    @Override
    @Before
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        waitForElementPresent(By.className("v-window"));

        window = $(WindowElement.class).first();
        cbDefault = $(CheckBoxElement.class).id("default");
        cbOther = $(CheckBoxElement.class).id("other");
        cbCtrl = $(CheckBoxElement.class).id("control");
        cbShift = $(CheckBoxElement.class).id("shift");
    }

    @Test
    public void testAllCheckBoxesSelected() {
        assertTrue("Default wasn't selected initially.", isChecked(cbDefault));
        assertTrue("Other wasn't selected initially.", isChecked(cbOther));
        assertTrue("Ctrl+A wasn't selected initially.", isChecked(cbCtrl));
        assertTrue("Shift+H wasn't selected initially.", isChecked(cbShift));
    }

    @Test
    public void testAllCheckBoxesClickable() {
        click(cbDefault);
        click(cbOther);
        click(cbCtrl);
        click(cbShift);

        assertFalse("Default was selected when it shouldn't have been.",
                isChecked(cbDefault));
        assertFalse("Other was selected when it shouldn't have been.",
                isChecked(cbOther));
        assertFalse("Ctrl+A was selected when it shouldn't have been.",
                isChecked(cbCtrl));
        assertFalse("Shift+H was selected when it shouldn't have been.",
                isChecked(cbShift));
    }

    @Test
    public void testDefaultWithAll() {
        attemptDefaultShortcut();
        ensureWindowClosed();
    }

    @Test
    public void testDefaultWithoutSelection() {
        click(cbDefault);

        attemptDefaultShortcut();
        ensureWindowOpen();
    }

    @Test
    public void testOtherWithAll() {
        attemptOtherShortcut();
        // TODO: remove this check once #14902 has been fixed
        if (!Browser.IE8.getDesiredCapabilities().equals(
                getDesiredCapabilities())
                && !Browser.FIREFOX.getDesiredCapabilities().equals(
                        getDesiredCapabilities())
                && !Browser.CHROME.getDesiredCapabilities().equals(
                        getDesiredCapabilities())) {
            ensureWindowClosed();
        }
    }

    @Test
    public void testOtherWithoutSelection() {
        click(cbOther);

        attemptOtherShortcut();
        ensureWindowOpen();
    }

    @Test
    public void testCtrlWithAll() {
        attemptCtrlShortcut();
        // TODO: remove this check once #14902 has been fixed
        if (Browser.PHANTOMJS.getDesiredCapabilities().equals(
                getDesiredCapabilities())) {
            ensureWindowClosed();
        }
    }

    @Test
    public void testCtrlWithoutSelection() {
        click(cbCtrl);

        attemptCtrlShortcut();
        ensureWindowOpen();
    }

    @Test
    public void testShiftWithAll() {
        attemptShiftShortcut();
        // TODO: remove this check once #14902 has been fixed
        if (getBrowsersExcludingIE().contains(getDesiredCapabilities())
                || Browser.IE8.getDesiredCapabilities().equals(
                        getDesiredCapabilities())) {
            ensureWindowClosed();
        }
    }

    @Test
    public void testShiftWithoutSelection() {
        click(cbShift);

        attemptShiftShortcut();
        ensureWindowOpen();
    }

    private boolean isChecked(CheckBoxElement cb) {
        String checked = cb.findElement(By.tagName("input")).getAttribute(
                "checked");
        if ("true".equals(checked)) {
            return true;
        } else if (checked == null) {
            return false;
        }
        throw new IllegalStateException(
                "Unexpected attribute value for 'checked': " + checked);
    }

    @Override
    protected void click(final CheckBoxElement cb) {
        final boolean initial = isChecked(cb);
        super.click(cb);
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return initial != isChecked(cb);
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return "checked state to change";
            }
        });
    }

    private void attemptDefaultShortcut() {
        window.focus();
        $(TextFieldElement.class).first().sendKeys(Keys.ESCAPE);
    }

    private void attemptOtherShortcut() {
        window.focus();
        $(TextFieldElement.class).first().sendKeys("R");
    }

    private void attemptCtrlShortcut() {
        window.focus();
        new Actions(driver).keyDown(Keys.CONTROL).perform();
        $(TextFieldElement.class).first().sendKeys("A");
        new Actions(driver).keyUp(Keys.CONTROL).perform();
    }

    private void attemptShiftShortcut() {
        window.focus();
        new Actions(driver).keyDown(Keys.SHIFT).perform();
        $(TextFieldElement.class).first().sendKeys("H");
        new Actions(driver).keyUp(Keys.SHIFT).perform();
    }

    private void ensureWindowClosed() {
        assertTrue("Window didn't close as expected.", $(WindowElement.class)
                .all().isEmpty());
    }

    private void ensureWindowOpen() {
        assertFalse("Window closed when it shouldn't have.",
                $(WindowElement.class).all().isEmpty());
    }
}
