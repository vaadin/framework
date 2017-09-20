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
package com.vaadin.tests.components.grid.basicfeatures.escalator;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.components.grid.basicfeatures.EscalatorBasicClientFeaturesTest;

/**
 * Test class to test the escalator level issue for ticket #16832
 */
public class EscalatorRemoveAndAddRowsTest
        extends EscalatorBasicClientFeaturesTest {

    @Before
    public void open() {
        openTestURL("theme=reindeer");
    }

    @Test
    public void testRemoveAllRowsAndAddThirtyThenScroll() throws IOException {
        selectMenuPath(GENERAL, POPULATE_COLUMN_ROW);

        scrollVerticallyTo(99999);
        assertTrue("Escalator is not scrolled to bottom.",
                isElementPresent(By.xpath("//td[text() = 'Row 99: 0,99']")));

        selectMenuPath(COLUMNS_AND_ROWS, BODY_ROWS, REMOVE_ALL_INSERT_SCROLL);

        scrollVerticallyTo(99999);
        assertTrue("Escalator is not scrolled to bottom.",
                isElementPresent(By.xpath("//td[text() = 'Row 29: 0,129']")));
    }

    @Test
    public void testRemoveRowsFromMiddle() {
        selectMenuPath(COLUMNS_AND_ROWS, COLUMNS, ADD_ONE_COLUMN_TO_BEGINNING);
        selectMenuPath(COLUMNS_AND_ROWS, HEADER_ROWS, ADD_ONE_ROW_TO_BEGINNING);
        selectMenuPath(COLUMNS_AND_ROWS, BODY_ROWS, ADD_22_ROWS_TO_TOP);
        // remove enough rows from middle, so that the total size of escalator
        // rows drops to below the size of the rows shown, forcing the escalator
        // to remove & move & update rows
        selectMenuPath(COLUMNS_AND_ROWS, BODY_ROWS, REMOVE_15_ROWS_FROM_MIDDLE);
        // first there was rows 0-21, then removed 15 rows 3-18, thus the rows
        // should be 0,1,2,18,19,20,21
        verifyRow(0, 0);
        verifyRow(1, 1);
        verifyRow(2, 2);
        verifyRow(3, 18);
        verifyRow(4, 19);
        verifyRow(5, 20);
        verifyRow(6, 21);
    }

    private void verifyRow(int escalatorIndex, int rowIndexInText) {
        // the format of text in cells is
        // Row: <index_when_updated>: <cell_index>,<index_when_inserted>
        Assert.assertEquals("Invalid row present in index " + escalatorIndex,
                "Row " + escalatorIndex + ": 0," + rowIndexInText,
                getBodyCell(escalatorIndex, 0).getText());
    }

}
