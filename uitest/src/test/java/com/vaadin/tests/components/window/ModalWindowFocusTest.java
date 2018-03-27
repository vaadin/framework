/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that a modal window is focused on creation and that on closing a window
 * focus is given to underlying modal window
 *
 * @author Vaadin Ltd
 */
public class ModalWindowFocusTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    }

    /**
     * First scenario: press first button -> two windows appear, press Esc two
     * times -> all windows should be closed
     */
    @Test
    public void testModalWindowFocusTwoWindows() throws IOException {

        waitForElementPresent(By.id("firstButton"));
        WebElement button = findElement(By.id("firstButton"));
        button.click();

        waitForElementPresent(By.id("windowButton"));
        assertTrue("Second window should be opened",
                findElements(By.id("windowButton")).size() == 1);

        pressKeyAndWait(Keys.ESCAPE);
        pressKeyAndWait(Keys.ESCAPE);
        assertTrue("All windows should be closed",
                findElements(By.className("v-window")).size() == 0);

    }

    /**
     * Second scenario: press first button -> two windows appear, press button
     * in the 2nd window -> 3rd window appears on top, press Esc three times ->
     * all windows should be closed
     */
    @Test
    public void testModalWindowFocusPressButtonInWindow() throws IOException {

        waitForElementPresent(By.id("firstButton"));
        WebElement button = findElement(By.id("firstButton"));
        button.click();

        waitForElementPresent(By.id("windowButton"));
        WebElement buttonInWindow = findElement(By.id("windowButton"));
        buttonInWindow.click();

        waitForElementPresent(By.id("window3"));
        assertTrue("Third window should be opened",
                findElements(By.id("window3")).size() == 1);

        pressKeyAndWait(Keys.ESCAPE);
        pressKeyAndWait(Keys.ESCAPE);
        pressKeyAndWait(Keys.ESCAPE);
        assertTrue("All windows should be closed",
                findElements(By.className("v-window")).size() == 0);

    }

    /**
     * Third scenario: press second button -> a modal unclosable and
     * unresizeable window with two text fields opens -> second text field is
     * automatically focused -> press tab -> the focus rolls around to the top
     * of the modal window -> the first text field is focused and shows a text
     */
    @Test
    public void testModalWindowWithoutButtonsFocusHandling() {
        waitForElementPresent(By.id("modalWindowButton"));
        WebElement button = findElement(By.id("modalWindowButton"));
        button.click();
        waitForElementPresent(By.id("focusfield"));
        pressKeyAndWait(Keys.TAB);
        TextFieldElement tfe = $(TextFieldElement.class).id("focusfield");
        assertTrue("First TextField should have received focus",
                "this has been focused".equals(tfe.getValue()));
    }

    private void pressKeyAndWait(Keys key) {
        new Actions(driver).sendKeys(key).build().perform();
        sleep(100);
    }

    @Test
    public void verifyAriaModalAndRoleAttributes() {
        waitForElementPresent(By.id("firstButton"));
        WebElement button = findElement(By.id("firstButton"));
        button.click();

        waitForElementPresent(By.className("v-window"));
        WebElement windowElement = findElement(By.className("v-window"));
        String ariaModal = windowElement.getAttribute("aria-modal");
        assertEquals("true", ariaModal);
        String role = windowElement.getAttribute("role");
        assertEquals("dialog", role);

    }

}
