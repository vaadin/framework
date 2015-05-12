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
package com.vaadin.tests.components.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that navigation with PageDown/PageUp/Home/End in Table works
 * 
 * @author Vaadin Ltd
 */
public class TableNavigationPageDownTest extends MultiBrowserTest {

    private static final int ROW_NUMBER = 50;
    private int lowerWrapperY = -1;
    private int pageHeight = -1;
    private int rowHeight = -1;

    private WebElement wrapper;

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Sending PageDown has no effect on PhantomJS. On IE focus
        // in Table is often lost, so default scrolling happens on PageDown.
        return getBrowserCapabilities(Browser.FIREFOX, Browser.CHROME);
    }

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();

        TableElement table = $(TableElement.class).first();
        rowHeight = table.getCell(1, 0).getLocation().getY()
                - table.getCell(0, 0).getLocation().getY();

        wrapper = findElement(By.className("v-table-body-wrapper"));
        pageHeight = wrapper.getSize().getHeight();
        lowerWrapperY = wrapper.getLocation().getY() + pageHeight;
    }

    private void sendKeyUntilEndIsReached(Keys key) {
        while (true) {
            int lastVisibleRowNumber = getLastVisibleRowNumber();
            sendKey(key);

            if (!waitUntilLastRowHasChanged(lastVisibleRowNumber)) {
                break;
            }
        }
    }

    private void sendKey(Keys key) {
        new Actions(driver).sendKeys(key).build().perform();
    }

    private boolean waitUntilLastRowHasChanged(final int row) {
        try {
            waitUntil(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver input) {
                    return row != getLastVisibleRowNumber();
                }
            }, 1);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private int getLastVisibleRowNumber() {
        return getRowNumber(getLastVisibleRow());
    }

    private void sendPageDownUntilBottomIsReached() {
        sendKeyUntilEndIsReached(Keys.PAGE_DOWN);
    }

    private void sendPageUpUntilTopIsReached() {
        sendKeyUntilEndIsReached(Keys.PAGE_UP);
    }

    @Test
    public void navigatePageDown() {
        // Scroll to a point where you can reach the bottom with a couple of
        // page downs.
        // Can't use v-table-body height because lower rows haven't been
        // fetched yet.
        testBenchElement(wrapper).scroll(
                ROW_NUMBER * rowHeight - (int) (2.8 * pageHeight));
        waitForScrollToFinish();

        getLastVisibleRow().click();
        sendPageDownUntilBottomIsReached();

        assertEquals("Last table row should be visible", ROW_NUMBER - 1,
                getLastVisibleRowNumber());
    }

    @Test
    public void navigatePageUp() {
        // Scroll to a point where you can reach the top with a couple of page
        // ups.
        testBenchElement(wrapper).scroll((int) (2.8 * pageHeight));
        waitForScrollToFinish();

        getFirstVisibleRow().click();
        sendPageUpUntilTopIsReached();

        assertEquals("First table row should be visible", 0,
                getRowNumber(getFirstVisibleRow()));
    }

    @Test
    public void navigateEndAndHome() {
        getLastVisibleRow().click();

        new Actions(driver).sendKeys(Keys.END).build().perform();
        waitForScrollToFinish();

        assertEquals("Last table row should be visible", ROW_NUMBER - 1,
                getRowNumber(getLastVisibleRow()));

        new Actions(driver).sendKeys(Keys.HOME).build().perform();
        waitForScrollToFinish();

        assertEquals("First table row should be visible", 0,
                getRowNumber(getFirstVisibleRow()));
    }

    /**
     * Waits until the scroll position indicator goes away, signifying that all
     * the required rows have been fetched.
     */
    private void waitForScrollToFinish() {
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                List<WebElement> elements = findElements(By
                        .className("v-table-scrollposition"));
                return elements.isEmpty() || !elements.get(0).isDisplayed();
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return "scroll position indicator to vanish";
            }
        });
    }

    /**
     * Returns row number from its first cell
     */
    private int getRowNumber(WebElement row) {
        return Integer.valueOf(row.findElement(
                By.className("v-table-cell-wrapper")).getText());
    }

    /**
     * Returns the first fully visible row
     */
    private WebElement getFirstVisibleRow() {
        List<WebElement> allFetchedRows = wrapper
                .findElements(By.tagName("tr"));
        int wrapperY = wrapper.getLocation().getY();
        for (WebElement row : allFetchedRows) {
            int rowY = row.getLocation().getY();
            if ((rowY >= wrapperY) && (rowY - rowHeight <= wrapperY)) {
                return row;
            }
        }
        fail("Could not find first visible row");
        return null;
    }

    /**
     * Returns the last fully visible row
     */
    private WebElement getLastVisibleRow() {
        List<WebElement> allFetchedRows = wrapper
                .findElements(By.tagName("tr"));
        for (WebElement row : allFetchedRows) {
            int lowerY = row.getLocation().getY() + rowHeight;
            if ((lowerY <= lowerWrapperY)
                    && (lowerY + rowHeight >= lowerWrapperY)) {
                return row;
            }
        }
        fail("Could not find last visible row");
        return null;
    }
}
