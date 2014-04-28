/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class BasicEscalatorTest extends MultiBrowserTest {

    @Test
    public void testInitialState() throws Exception {
        openTestURL();

        WebElement cell1 = getBodyRowCell(0, 0);
        assertEquals("Top left body cell had unexpected content", "Row 0: 0,0",
                cell1.getText());

        WebElement cell2 = getBodyRowCell(15, 3);
        assertEquals("Lower merged cell had unexpected content", "Cell: 3,15",
                cell2.getText());
    }

    @Test
    public void testScroll() throws Exception {
        openTestURL();

        /*
         * let the DOM stabilize itself. TODO: remove once waitForVaadin
         * supports lazy loaded components
         */
        Thread.sleep(100);

        scrollEscalatorVerticallyTo(1000);
        assertBodyCellWithContentIsFound("Row 50: 0,50");
    }

    @Test
    public void testLastRow() throws Exception {
        openTestURL();

        /*
         * let the DOM stabilize itself. TODO: remove once waitForVaadin
         * supports lazy loaded components
         */
        Thread.sleep(100);

        // scroll to bottom
        scrollEscalatorVerticallyTo(100000000);

        /*
         * this test does not test DOM reordering, therefore we don't rely on
         * child indices - we simply seek by content.
         */
        assertBodyCellWithContentIsFound("Row 99: 0,99");
    }

    @Test
    public void testNormalRowHeight() throws Exception {
        /*
         * This is tested with screenshots instead of CSS queries, since some
         * browsers report dimensions differently from each other, which is
         * uninteresting for our purposes
         */
        openTestURL();
        compareScreen("normalHeight");
    }

    @Test
    public void testModifiedRowHeight() throws Exception {
        /*
         * This is tested with screenshots instead of CSS queries, since some
         * browsers report dimensions differently from each other, which is
         * uninteresting for our purposes
         */
        openTestURLWithTheme("reindeer-tests");
        compareScreen("modifiedHeight");
    }

    private void assertBodyCellWithContentIsFound(String cellContent) {
        String xpath = "//tbody/tr/td[.='" + cellContent + "']";
        try {
            assertNotNull("received a null element with \"" + xpath + "\"",
                    getDriver().findElement(By.xpath(xpath)));
        } catch (NoSuchElementException e) {
            fail("Could not find '" + xpath + "'");
        }
    }

    private WebElement getBodyRowCell(int row, int col) {
        return getDriver().findElement(
                By.xpath("//tbody/tr[@class='v-escalator-row'][" + (row + 1)
                        + "]/td[" + (col + 1) + "]"));
    }

    private void openTestURLWithTheme(String themeName) {
        String testUrl = getTestUrl();
        testUrl += (testUrl.contains("?")) ? "&" : "?";
        testUrl += "theme=" + themeName;
        getDriver().get(testUrl);
    }

    private void scrollEscalatorVerticallyTo(double px) {
        executeScript("arguments[0].scrollTop = " + px,
                getGridVerticalScrollbar());
    }

    private Object executeScript(String script, WebElement element) {
        @SuppressWarnings("hiding")
        final WebDriver driver = getDriver();
        if (driver instanceof JavascriptExecutor) {
            final JavascriptExecutor je = (JavascriptExecutor) driver;
            return je.executeScript(script, element);
        } else {
            throw new IllegalStateException("current driver "
                    + getDriver().getClass().getName() + " is not a "
                    + JavascriptExecutor.class.getSimpleName());
        }
    }

    private WebElement getGridVerticalScrollbar() {
        return getDriver()
                .findElement(
                        By.xpath("//div[contains(@class, \"v-escalator-scroller-vertical\")]"));
    }
}
