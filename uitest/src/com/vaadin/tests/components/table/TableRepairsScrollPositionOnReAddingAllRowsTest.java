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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Scroll position should be restored when removing and re-adding all rows in
 * Table.
 * 
 * @author Vaadin Ltd
 */
public class TableRepairsScrollPositionOnReAddingAllRowsTest extends
        MultiBrowserTest {

    private int rowLocation0;

    @Override
    @Before
    public void setup() throws Exception {
        super.setup();
        openTestURL();

        rowLocation0 = getCellY(0);
        scrollToBottom();
    }

    @Test
    public void testReAddAllViaAddAll() {
        int rowLocation = getCellY(70);

        // This button is for re-adding all rows (original itemIds) at once
        // (removeAll() + addAll())
        hitButton("buttonReAddAllViaAddAll");

        int newRowLocation = getCellY(70);

        assertCloseTo(
                "Scroll position should be the same as before Re-Adding rows via addAll()",
                newRowLocation, rowLocation);

    }

    @Test
    public void testReplaceByAnotherCollectionViaAddAll() {
        int rowLocation = getCellY(70);

        // This button is for replacing all rows at once (removeAll() +
        // addAll())
        hitButton("buttonReplaceByAnotherCollectionViaAddAll");

        // new collection has one less element
        int newRowLocation = getCellY(69);

        assertCloseTo(
                "Scroll position should be the same as before Replacing rows via addAll()",
                newRowLocation, rowLocation);
    }

    @Test
    public void testReplaceByAnotherCollectionViaAdd() {

        // This button is for replacing all rows one by one (removeAll() + add()
        // + add()..)
        hitButton("buttonReplaceByAnotherCollectionViaAdd");

        int newRowLocation = getCellY(0);

        assertCloseTo("Scroll position should be 0", newRowLocation,
                rowLocation0);
    }

    @Test
    public void testReplaceBySubsetOfSmallerSize() {
        // This button is for replacing all rows at once but the count of rows
        // is less then first index to scroll
        hitButton("buttonReplaceBySubsetOfSmallerSize");

        int newRowLocation = getCellY(5);

        assertCloseTo("Scroll position should be 0", newRowLocation,
                rowLocation0);
    }

    @Test
    public void testReplaceByWholeSubsetPlusOneNew() {
        int rowLocation = getCellY(70);

        // This button is for replacing by whole original sub-set of items plus
        // one new
        hitButton("buttonReplaceByWholeSubsetPlusOneNew");

        int newRowLocation = getCellY(70);

        assertCloseTo("Scroll position should be the same as before Replacing",
                newRowLocation, rowLocation);

    }

    @Test
    public void testRemoveAllAddOne() {
        // This button is for removing all and then adding only one new item
        hitButton("buttonRemoveAllAddOne");

        int newRowLocation = getCellY(0);

        assertCloseTo("Scroll position should be 0", newRowLocation,
                rowLocation0);
    }

    @Test
    public void testReplaceByNewDatasource() {
        // This button is for remove all items and add new datasource
        hitButton("buttonReplaceByNewDatasource");

        int newRowLocation = getCellY(0);

        assertCloseTo("Scroll position should be 0", newRowLocation,
                rowLocation0);
    }

    private TableElement getTable() {
        return $(TableElement.class).first();
    }

    private void scrollToBottom() {
        scrollTable(getTable(), 80, 70);
    }

    private int getCellY(int row) {
        return getTable().getCell(row, 0).getLocation().getY();
    }

    private void assertCloseTo(String reason, int actual, int expected) {
        // ranged check because IE9 consistently misses the mark by 1 pixel
        assertThat(reason, (double) actual, closeTo(expected, 1));
    }

}
