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
package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.annotations.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class BasicEscalatorTest extends MultiBrowserTest {

    private static final int SLEEP = 300;

    private static final Pattern ROW_PATTERN = Pattern
            .compile("Row (\\d+): \\d+,\\d+");

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

        setScrollTop(getVerticalScrollbar(), 1000);
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
        setScrollTop(getVerticalScrollbar(), 100000000);

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

    @Test
    public void domIsInitiallySorted() throws Exception {
        openTestURL();

        final List<WebElement> rows = getBodyRows();
        assertTrue("no body rows found", !rows.isEmpty());
        for (int i = 0; i < rows.size(); i++) {
            String text = rows.get(i).getText();
            String expected = "Row " + i;
            assertTrue("Expected \"" + expected + "...\" but was " + text,
                    text.startsWith(expected));
        }
    }

    @Test
    public void domIsSortedAfterInsert() throws Exception {
        openTestURL();

        final int rowsToInsert = 5;
        final int offset = 5;
        insertRows(offset, rowsToInsert);

        final List<WebElement> rows = getBodyRows();
        int i = 0;
        for (; i < offset + rowsToInsert; i++) {
            final String expectedStart = "Row " + i;
            final String text = rows.get(i).getText();
            assertTrue("Expected \"" + expectedStart + "...\" but was " + text,
                    text.startsWith(expectedStart));
        }

        for (; i < rows.size(); i++) {
            final String expectedStart = "Row " + (i - rowsToInsert);
            final String text = rows.get(i).getText();
            assertTrue("(post insert) Expected \"" + expectedStart
                    + "...\" but was " + text, text.startsWith(expectedStart));
        }
    }

    @Test
    public void domIsSortedAfterRemove() throws Exception {
        openTestURL();

        final int rowsToRemove = 5;
        final int offset = 5;
        removeRows(offset, rowsToRemove);

        final List<WebElement> rows = getBodyRows();
        int i = 0;
        for (; i < offset; i++) {
            final String expectedStart = "Row " + i;
            final String text = rows.get(i).getText();
            assertTrue("Expected " + expectedStart + "... but was " + text,
                    text.startsWith(expectedStart));
        }

        /*
         * We check only up to 10, since after that, the indices are again
         * reset, because new rows have been generated. The row numbers that
         * they are given depends on the widget size, and it's too fragile to
         * rely on some special assumptions on that.
         */
        for (; i < 10; i++) {
            final String expectedStart = "Row " + (i + rowsToRemove);
            final String text = rows.get(i).getText();
            assertTrue("(post remove) Expected " + expectedStart
                    + "... but was " + text, text.startsWith(expectedStart));
        }
    }

    @Test
    public void domIsSortedAfterScroll() throws Exception {
        openTestURL();
        setScrollTop(getVerticalScrollbar(), 500);

        /*
         * Let the DOM reorder itself.
         * 
         * TODO TestBench currently doesn't know when Grid's DOM structure is
         * stable. There are some plans regarding implementing support for this,
         * so this test case can (should) be modified once that's implemented.
         */
        sleep(SLEEP);

        List<WebElement> rows = getBodyRows();
        int firstRowNumber = parseFirstRowNumber(rows);

        for (int i = 0; i < rows.size(); i++) {
            final String expectedStart = "Row " + (i + firstRowNumber);
            final String text = rows.get(i).getText();
            assertTrue("(post remove) Expected " + expectedStart
                    + "... but was " + text, text.startsWith(expectedStart));
        }
    }

    private static int parseFirstRowNumber(List<WebElement> rows)
            throws NumberFormatException {
        final WebElement firstRow = rows.get(0);
        final String firstRowText = firstRow.getText();
        final Matcher matcher = ROW_PATTERN.matcher(firstRowText);
        if (!matcher.find()) {
            fail("could not find " + ROW_PATTERN.pattern() + " in \""
                    + firstRowText + "\"");
        }
        final String number = matcher.group(1);
        return Integer.parseInt(number);
    }

    private void insertRows(final int offset, final int amount) {
        final WebElement offsetInput = vaadinElementById(BasicEscalator.INSERT_ROWS_OFFSET);
        offsetInput.sendKeys(String.valueOf(offset), Keys.RETURN);

        final WebElement amountInput = vaadinElementById(BasicEscalator.INSERT_ROWS_AMOUNT);
        amountInput.sendKeys(String.valueOf(amount), Keys.RETURN);

        final WebElement button = vaadinElementById(BasicEscalator.INSERT_ROWS_BUTTON);
        button.click();
    }

    private void removeRows(final int offset, final int amount) {
        final WebElement offsetInput = vaadinElementById(BasicEscalator.REMOVE_ROWS_OFFSET);
        offsetInput.sendKeys(String.valueOf(offset), Keys.RETURN);

        final WebElement amountInput = vaadinElementById(BasicEscalator.REMOVE_ROWS_AMOUNT);
        amountInput.sendKeys(String.valueOf(amount), Keys.RETURN);

        final WebElement button = vaadinElementById(BasicEscalator.REMOVE_ROWS_BUTTON);
        button.click();
    }

    private void setScrollTop(WebElement element, long px) {
        executeScript("arguments[0].scrollTop = " + px, element);
    }

    private List<WebElement> getBodyRows() {
        return getDriver().findElements(By.xpath("//tbody/tr/td[1]"));
    }

    private WebElement getVerticalScrollbar() {
        return getDriver().findElement(
                By.xpath("//div["
                        + "contains(@class, 'v-escalator-scroller-vertical')"
                        + "]"));
    }
}
