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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ItemClickEventsTest extends MultiBrowserTest {

    @Before
    public void init() {
        openTestURL("restartApplication");
    }

    private void clickElement(TestBenchElement e) {
        assertNotNull(e);
        e.click(5, 5);
    }

    private void doubleClickElement(TestBenchElement e) {
        assertNotNull(e);
        e.doubleClick();
    }

    private void assertLog(String compare) {
        LabelElement logRow = $(LabelElement.class).id("Log_row_0");
        assertNotNull(logRow);
        assertTrue(logRow.getText().contains(compare));
    }

    private void assertSelected(TestBenchElement e) {
        assertNotNull(e);
        assertTrue(hasCssClass(e, "v-selected"));
    }

    @Test
    public void testSingleSelectNull() throws Exception {

        // Activate table null selection mode
        clickElement($(CheckBoxElement.class).caption("nullsel").get(1));

        // Get at the table element
        TableElement table = $(TableElement.class).id("table");

        // Select the first item
        clickElement(table.getRow(0));
        assertLog("left click on table/Item 0");

        // Do it again
        clickElement(table.getRow(0));
        assertLog("left click on table/Item 0");

        // Select the sixth item
        clickElement(table.getRow(5));
        assertLog("left click on table/Item 5");

        // Double click the sixth item
        doubleClickElement(table.getRow(5));
        assertLog("doubleClick on table/Item 5");
    }

    @Test
    public void testSingleSelectNotNull() throws Exception {
        // Get reference to table
        TableElement table = $(TableElement.class).id("table");

        // Select first item in list
        clickElement(table.getRow(0));
        assertSelected(table.getRow(0));

        // Check that the log contains "clicked item 0"
        assertLog("left click on table/Item 0");

        // Click on second item in list
        clickElement(table.getRow(1));

        // Make sure it got selected
        assertSelected(table.getRow(1));

        // Check log output
        assertLog("left click on table/Item 1");

        // Click row 1 again
        clickElement(table.getRow(1));
        assertLog("left click on table/Item 1");

        // Test double click
        doubleClickElement(table.getRow(1));
        // kludge: testbench seems to send an extra click; that doesn't affect
        // our test too much, though, and can be ignored.
        assertLog("doubleClick on table/Item 1");

        // Double click first item
        doubleClickElement(table.getRow(0));
        assertLog("doubleClick on table/Item 0");

        // Make sure it got selected again
        assertSelected(table.getRow(0));
    }

    @Test
    public void testSingleSelectNotSelectable() throws Exception {

        // Remove the 'selectable' mode from Table
        $(CheckBoxElement.class).caption("selectable").get(1).click();

        // Get table element
        TableElement table = $(TableElement.class).id("table");

        // Click some items and check that clicks go through
        clickElement(table.getCell(0, 0));
        assertLog("left click on table/Item 0");

        clickElement(table.getCell(5, 0));
        assertLog("left click on table/Item 5");

        clickElement(table.getCell(2, 0));
        assertLog("left click on table/Item 2");

        clickElement(table.getCell(8, 0));
        assertLog("left click on table/Item 8");

        clickElement(table.getCell(1, 0));
        assertLog("left click on table/Item 1");

        clickElement(table.getCell(0, 0));
        assertLog("left click on table/Item 0");

    }

    @Test
    public void testNonImmediateSingleSelectable() throws Exception {

        // Disable table immediate mode
        clickElement($(CheckBoxElement.class).caption("immediate").get(1));

        // Get table element
        TableElement table = $(TableElement.class).id("table");

        // Click items and verify that click event went through
        clickElement(table.getCell(1, 0));
        assertLog("left click on table/Item 1");

        clickElement(table.getCell(8, 0));
        assertLog("left click on table/Item 8");

        clickElement(table.getCell(1, 0));
        assertLog("left click on table/Item 1");

        clickElement(table.getCell(0, 0));
        assertLog("left click on table/Item 0");

        clickElement(table.getCell(6, 0));
        assertLog("left click on table/Item 6");

    }

}
