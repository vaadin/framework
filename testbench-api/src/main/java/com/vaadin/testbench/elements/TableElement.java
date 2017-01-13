/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.testbench.elements;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.AbstractElement;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.Table")
@Deprecated
public class TableElement extends AbstractSelectElement {

    /**
     * Function to find a Table cell. Looking for a cell that is currently not
     * visible will throw NoSuchElementException
     *
     * @param row
     *            0 based row index
     * @param column
     *            0 based column index
     * @return TestBenchElement containing wanted cell.
     * @throws NoSuchElementException
     *             if the cell (row, column) is not found.
     */
    public TestBenchElement getCell(int row, int column) {

        TestBenchElement cell = wrapElement(
                findElement(By.vaadin("#row[" + row + "]/col[" + column + "]")),
                getCommandExecutor());

        return cell;
    }

    /**
     * Return table row element by zero-based index
     *
     * @return table row element by zero-based index
     */
    public TableRowElement getRow(int row) {
        TestBenchElement rowElem = wrapElement(
                findElement(By.vaadin("#row[" + row + "]")),
                getCommandExecutor());
        return rowElem.wrap(TableRowElement.class);
    }

    /**
     * Returns the header cell with the given column index.
     *
     * @param column
     *            0 based column index
     * @return TableHeaderElement containing the wanted header cell
     */
    public TableHeaderElement getHeaderCell(int column) {
        TestBenchElement headerCell = wrapElement(
                findElement(By.vaadin("#header[" + column + "]")),
                getCommandExecutor());
        return headerCell.wrap(TableHeaderElement.class);
    }

    /**
     * Function to get footer cell with given column index
     *
     * @param column
     *            0 based column index
     * @return TestBenchElement containing wanted footer cell
     */
    public TestBenchElement getFooterCell(int column) {
        TestBenchElement footerCell = wrapElement(
                findElement(By.vaadin("#footer[" + column + "]")),
                getCommandExecutor());
        return footerCell;
    }

    @Override
    public void scroll(int scrollTop) {
        ((TestBenchElement) findElement(By.className("v-scrollable")))
                .scroll(scrollTop);
    }

    @Override
    public void scrollLeft(int scrollLeft) {
        ((TestBenchElement) findElement(By.className("v-scrollable")))
                .scrollLeft(scrollLeft);
    }

    @Override
    public void contextClick() {
        WebElement tbody = findElement(By.className("v-table-body"));
        // There is a problem in with phantomjs driver, just calling
        // contextClick() doesn't work. We have to use javascript.
        if (isPhantomJS()) {
            JavascriptExecutor js = getCommandExecutor();
            String scr = "var element=arguments[0];"
                    + "var ev = document.createEvent('HTMLEvents');"
                    + "ev.initEvent('contextmenu', true, false);"
                    + "element.dispatchEvent(ev);";
            js.executeScript(scr, tbody);
        } else {
            new Actions(getDriver()).contextClick(tbody).build().perform();
        }
    }

    public static class ContextMenuElement extends AbstractElement {

        public WebElement getItem(int index) {
            return findElement(
                    By.xpath(".//table//tr[" + (index + 1) + "]//td/*"));
        }

    }

    /**
     * Fetches the context menu for the table
     *
     * @return {@link com.vaadin.testbench.elements.TableElement.ContextMenuElement}
     * @throws java.util.NoSuchElementException
     *             if the menu isn't open
     */
    public ContextMenuElement getContextMenu() {
        try {
            WebElement cm = getDriver()
                    .findElement(By.className("v-contextmenu"));
            return wrapElement(cm, getCommandExecutor())
                    .wrap(ContextMenuElement.class);
        } catch (WebDriverException e) {
            throw new NoSuchElementException("Context menu not found", e);
        }
    }

    /**
     * Opens the collapse menu of this table and returns the element for it.
     *
     * @return collapse menu element
     */
    public CollapseMenuElement openCollapseMenu() {
        getCollapseMenuToggle().click();
        WebElement cm = getDriver()
                .findElement(By.xpath("//*[@id='PID_VAADIN_CM']"));
        return wrapElement(cm, getCommandExecutor())
                .wrap(CollapseMenuElement.class);
    }

    /**
     * Element representing a collapse menu of a Table.
     */
    public static class CollapseMenuElement extends ContextMenuElement {
    }

    /**
     * Gets the button that shows or hides the collapse menu.
     *
     * @return button for opening collapse menu
     */
    public WebElement getCollapseMenuToggle() {
        return findElement(By.className("v-table-column-selector"));
    }

}
