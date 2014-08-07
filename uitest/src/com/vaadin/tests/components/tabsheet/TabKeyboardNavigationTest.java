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
package com.vaadin.tests.components.tabsheet;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Add TB3 test as the TB2 one failed on keyboard events.
 *
 * @since
 * @author Vaadin Ltd
 */
public class TabKeyboardNavigationTest extends MultiBrowserTest {

    @Test
    public void testFocus() throws InterruptedException, IOException {
        openTestURL();

        click(1);
        sendKeys(1, Keys.ARROW_RIGHT);

        assertSheet(1);
        sendKeys(2, Keys.SPACE);
        assertSheet(2);
        compareScreen("tab2");

        sendKeys(2, Keys.ARROW_RIGHT);
        sendKeys(3, Keys.ARROW_RIGHT);
        assertSheet(2);

        sendKeys(5, Keys.SPACE);
        assertSheet(5);
        compareScreen("skip-disabled-to-tab5");

        TestBenchElement addTabButton = (TestBenchElement) getDriver()
                .findElements(By.className("v-button")).get(0);

        click(addTabButton);

        click(5);
        sendKeys(5, Keys.ARROW_RIGHT);
        assertSheet(5);

        sendKeys(6, Keys.SPACE);
        assertSheet(6);

        click(addTabButton);
        click(addTabButton);
        click(addTabButton);
        click(addTabButton);
        click(addTabButton);
        click(addTabButton);

        click(8);
        compareScreen("click-tab-8");

        sendKeys(8, Keys.ARROW_RIGHT);
        sendKeys(9, Keys.SPACE);
        click(9);
        compareScreen("tab-9");

        sendKeys(9, Keys.ARROW_RIGHT);
        Thread.sleep(DELAY);

        sendKeys(10, Keys.ARROW_RIGHT);

        // Here PhantomJS used to fail. Or when accessing tab2. The fix was to
        // call the elem.click(x, y) using the (x, y) position instead of the
        // elem.click() without any arguments.
        sendKeys(11, Keys.ARROW_RIGHT);

        assertSheet(9);
        sendKeys(12, Keys.SPACE);
        assertSheet(12);
        compareScreen("scrolled-right-to-tab-12");

        click(5);

        sendKeys(5, Keys.ARROW_LEFT);

        // Here IE8 used to fail. A hidden <div> in IE8 would have the bounds of
        // it's parent, and when trying to see in which direction to scroll
        // (left or right) to make the key selected tab visible, the
        // VTabSheet.scrollIntoView(Tab) used to check first whether the tab
        // isClipped. On IE8 this will always return true for both hidden tabs
        // on the left and clipped tabs on the right. So instead of going to
        // left, it'll search all the way to the right.
        sendKeys(3, Keys.ARROW_LEFT);
        sendKeys(2, Keys.ARROW_LEFT);
        assertSheet(5);

        sendKeys(1, Keys.SPACE);
        assertSheet(1);
        compareScreen("scrolled-left-to-tab-1");
    }

    /*
     * Press key on the element.
     */
    private void sendKeys(int tabIndex, Keys key) throws InterruptedException {
        sendKeys(tab(tabIndex), key);
    }

    /*
     * Press key on the element.
     */
    private void sendKeys(TestBenchElement element, Keys key)
            throws InterruptedException {

        element.sendKeys(key);
        if (DELAY > 0) {
            sleep(DELAY);
        }
    }

    /*
     * Click on the element.
     */
    private void click(int tabIndex) throws InterruptedException {
        click(tab(tabIndex));
    }

    /*
     * Click on the element.
     */
    private void click(TestBenchElement element) throws InterruptedException {

        element.click(10, 10);
        if (DELAY > 0) {
            sleep(DELAY);
        }
    }

    /*
     * Delay for PhantomJS.
     */
    private final static int DELAY = 10;

    private void assertSheet(int index) {
        String labelCaption = "Tab " + index;

        By id = By.id(TabKeyboardNavigation.labelID(index));
        WebElement labelElement = getDriver().findElement(id);

        waitForElementPresent(id);

        Assert.assertEquals(labelCaption, labelCaption, labelElement.getText());
    }

    /*
     * Provide the tab at specified index.
     */
    private TestBenchElement tab(int index) {
        By by = By.className("v-tabsheet-tabitemcell");

        TestBenchElement element = (TestBenchElement) getDriver().findElements(
                by).get(index - 1);

        String expected = "Tab " + index;
        Assert.assertEquals(expected,
                element.getText().substring(0, expected.length()));

        return element;
    }

}
