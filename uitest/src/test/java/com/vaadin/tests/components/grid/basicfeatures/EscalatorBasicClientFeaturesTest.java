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
package com.vaadin.tests.components.grid.basicfeatures;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public abstract class EscalatorBasicClientFeaturesTest extends MultiBrowserTest {

    private static final String LOGICAL_ROW_ATTRIBUTE_NAME = "vLogicalRow";
    private static final String SPACER_CSS_CLASS = "v-escalator-spacer";

    protected static final String COLUMNS_AND_ROWS = "Columns and Rows";

    protected static final String COLUMNS = "Columns";
    protected static final String ADD_ONE_COLUMN_TO_BEGINNING = "Add one column to beginning";
    protected static final String ADD_ONE_ROW_TO_BEGINNING = "Add one row to beginning";
    protected static final String ADD_ONE_ROW_TO_END = "Add one row to end";
    protected static final String REMOVE_ONE_COLUMN_FROM_BEGINNING = "Remove one column from beginning";
    protected static final String REMOVE_ONE_ROW_FROM_BEGINNING = "Remove one row from beginning";
    protected static final String REMOVE_ALL_ROWS = "Remove all rows";
    protected static final String REMOVE_50_ROWS_FROM_BOTTOM = "Remove 50 rows from bottom";
    protected static final String REMOVE_50_ROWS_FROM_ALMOST_BOTTOM = "Remove 50 rows from almost bottom";
    protected static final String ADD_ONE_OF_EACH_ROW = "Add one of each row";
    protected static final String RESIZE_FIRST_COLUMN_TO_MAX_WIDTH = "Resize first column to max width";
    protected static final String RESIZE_FIRST_COLUMN_TO_100PX = "Resize first column to 100 px";

    protected static final String HEADER_ROWS = "Header Rows";
    protected static final String BODY_ROWS = "Body Rows";
    protected static final String FOOTER_ROWS = "Footer Rows";

    protected static final String SCROLL_TO = "Scroll to...";

    protected static final String REMOVE_ALL_INSERT_SCROLL = "Remove all, insert 30 and scroll 40px";

    protected static final String GENERAL = "General";
    protected static final String DETACH_ESCALATOR = "Detach Escalator";
    protected static final String ATTACH_ESCALATOR = "Attach Escalator";
    protected static final String POPULATE_COLUMN_ROW = "Populate Escalator (columns, then rows)";
    protected static final String POPULATE_ROW_COLUMN = "Populate Escalator (rows, then columns)";
    protected static final String CLEAR_COLUMN_ROW = "Clear (columns, then rows)";
    protected static final String CLEAR_ROW_COLUMN = "Clear (rows, then columns)";

    protected static final String FEATURES = "Features";
    protected static final String FROZEN_COLUMNS = "Frozen columns";
    protected static final String FREEZE_1_COLUMN = "Freeze 1 column";
    protected static final String FREEZE_0_COLUMNS = "Freeze 0 columns";
    protected static final String COLUMN_SPANNING = "Column spanning";
    protected static final String COLSPAN_NORMAL = "Apply normal colspan";
    protected static final String COLSPAN_NONE = "Apply no colspan";
    protected static final String SET_100PX = "Set 100px";
    protected static final String SPACERS = "Spacers";
    protected static final String FOCUSABLE_UPDATER = "Focusable Updater";
    protected static final String SCROLL_HERE_ANY_0PADDING = "Scroll here (ANY, 0)";
    protected static final String SCROLL_HERE_SPACERBELOW_ANY_0PADDING = "Scroll here row+spacer below (ANY, 0)";
    protected static final String REMOVE = "Remove";

    protected static final String ROW_MINUS1 = "Row -1";
    protected static final String ROW_0 = "Row 0";
    protected static final String ROW_1 = "Row 1";
    protected static final String ROW_25 = "Row 25";
    protected static final String ROW_50 = "Row 50";
    protected static final String ROW_75 = "Row 75";
    protected static final String ROW_99 = "Row 99";

    @Override
    public void setup() throws Exception {
        super.setup();

        setDebug(true);
    }

    @Override
    protected Class<?> getUIClass() {
        return EscalatorBasicClientFeatures.class;
    }

    protected TestBenchElement getEscalator() {
        By className = By.className("v-escalator");
        if (isElementPresent(className)) {
            return (TestBenchElement) findElement(className);
        }
        return null;
    }

    /**
     * @param row
     *            the index of the row element in the section. If negative, the
     *            calculation starts from the end (-1 is the last, -2 is the
     *            second-to-last etc)
     */
    protected TestBenchElement getHeaderRow(int row) {
        return getRow("thead", row);
    }

    /**
     * @param row
     *            the index of the row element in the section. If negative, the
     *            calculation starts from the end (-1 is the last, -2 is the
     *            second-to-last etc)
     */
    protected TestBenchElement getBodyRow(int row) {
        return getRow("tbody", row);
    }

    /**
     * @param row
     *            the index of the row element in the section. If negative, the
     *            calculation starts from the end (-1 is the last, -2 is the
     *            second-to-last etc)
     */
    protected TestBenchElement getFooterRow(int row) {
        return getRow("tfoot", row);
    }

    /**
     * @param row
     *            the index of the row element in the section. If negative, the
     *            calculation starts from the end (-1 is the last, -2 is the
     *            second-to-last etc)
     */
    protected TestBenchElement getHeaderCell(int row, int col) {
        return getCell("thead", row, col);
    }

    /**
     * @param row
     *            the index of the row element in the section. If negative, the
     *            calculation starts from the end (-1 is the last, -2 is the
     *            second-to-last etc)
     */
    protected TestBenchElement getBodyCell(int row, int col) {
        return getCell("tbody", row, col);
    }

    /**
     * @param row
     *            the index of the row element in the section. If negative, the
     *            calculation starts from the end (-1 is the last, -2 is the
     *            second-to-last etc)
     */
    protected TestBenchElement getFooterCell(int row, int col) {
        return getCell("tfoot", row, col);
    }

    /**
     * @param row
     *            the index of the row element in the section. If negative, the
     *            calculation starts from the end (-1 is the last, -2 is the
     *            second-to-last etc)
     */
    private TestBenchElement getCell(String sectionTag, int row, int col) {
        TestBenchElement rowElement = getRow(sectionTag, row);
        By xpath = By.xpath("*[" + (col + 1) + "]");
        if (rowElement != null && rowElement.isElementPresent(xpath)) {
            return (TestBenchElement) rowElement.findElement(xpath);
        }
        return null;
    }

    /**
     * @param row
     *            the index of the row element in the section. If negative, the
     *            calculation starts from the end (-1 is the last, -2 is the
     *            second-to-last etc)
     */
    private TestBenchElement getRow(String sectionTag, int row) {
        TestBenchElement escalator = getEscalator();
        WebElement tableSection = escalator.findElement(By.tagName(sectionTag));

        String xpathExpression = "tr[not(@class='" + SPACER_CSS_CLASS + "')]";
        if (row >= 0) {
            int fromFirst = row + 1;
            xpathExpression += "[" + fromFirst + "]";
        } else {
            int fromLast = Math.abs(row + 1);
            xpathExpression += "[last() - " + fromLast + "]";
        }
        By xpath = By.xpath(xpathExpression);
        if (tableSection != null
                && ((TestBenchElement) tableSection).isElementPresent(xpath)) {
            return (TestBenchElement) tableSection.findElement(xpath);
        }
        return null;
    }

    @Override
    protected void selectMenu(String menuCaption) {
        // GWT menu does not need to be clicked.
        selectMenu(menuCaption, false);
    }

    @Override
    protected WebElement getMenuElement(String menuCaption) {
        return getDriver().findElement(
                By.xpath("//td[text() = '" + menuCaption + "']"));
    }

    protected void assertLogContains(String substring) {
        assertTrue("log should've contained, but didn't: " + substring,
                getLogText().contains(substring));
    }

    protected void assertLogDoesNotContain(String substring) {
        assertFalse("log shouldn't have contained, but did: " + substring,
                getLogText().contains(substring));
    }

    private String getLogText() {
        WebElement log = findElement(By.cssSelector("#log"));
        return log.getText();
    }

    protected void assertLogContainsInOrder(String... substrings) {
        String log = getLogText();
        int cursor = 0;
        for (String substring : substrings) {
            String remainingLog = log.substring(cursor, log.length());
            int substringIndex = remainingLog.indexOf(substring);
            if (substringIndex == -1) {
                fail("substring \"" + substring
                        + "\" was not found in order from log.");
            }

            cursor += substringIndex + substring.length();
        }
    }

    protected void scrollVerticallyTo(int px) {
        getVerticalScrollbar().scroll(px);
    }

    protected long getScrollTop() {
        return ((Long) executeScript("return arguments[0].scrollTop;",
                getVerticalScrollbar())).longValue();
    }

    private TestBenchElement getVerticalScrollbar() {
        return (TestBenchElement) getEscalator().findElement(
                By.className("v-escalator-scroller-vertical"));
    }

    protected void scrollHorizontallyTo(int px) {
        getHorizontalScrollbar().scrollLeft(px);
    }

    protected long getScrollLeft() {
        return ((Long) executeScript("return arguments[0].scrollLeft;",
                getHorizontalScrollbar())).longValue();
    }

    protected TestBenchElement getHorizontalScrollbar() {
        return (TestBenchElement) getEscalator().findElement(
                By.className("v-escalator-scroller-horizontal"));
    }

    @Override
    protected Object executeScript(String script, Object... args) {
        return ((JavascriptExecutor) getDriver()).executeScript(script, args);
    }

    protected void populate() {
        selectMenuPath(GENERAL, POPULATE_COLUMN_ROW);
    }

    private List<WebElement> getSpacers() {
        return getEscalator().findElements(By.className(SPACER_CSS_CLASS));
    }

    protected boolean spacersAreFoundInDom() {
        List<WebElement> spacers = getSpacers();
        return spacers != null && !spacers.isEmpty();
    }

    @SuppressWarnings("boxing")
    protected WebElement getSpacer(int logicalRowIndex) {
        List<WebElement> spacers = getSpacers();
        System.out.println("size: " + spacers.size());
        for (WebElement spacer : spacers) {
            System.out.println(spacer + ", " + logicalRowIndex);
            Boolean isInDom = (Boolean) executeScript("return arguments[0]['"
                    + LOGICAL_ROW_ATTRIBUTE_NAME + "'] === arguments[1]",
                    spacer, logicalRowIndex);
            if (isInDom) {
                return spacer;
            }
        }
        return null;
    }
}
