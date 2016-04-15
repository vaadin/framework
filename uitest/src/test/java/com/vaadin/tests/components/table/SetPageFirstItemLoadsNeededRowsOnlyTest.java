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

import org.junit.Test;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * 
 * @author Vaadin Ltd
 */
public class SetPageFirstItemLoadsNeededRowsOnlyTest extends MultiBrowserTest {

    /*
     * expectedRowsRequested is related to VScrollTable's cache_rate and
     * pageLength. See for instance VScrollTable.ensureCacheFilled().
     * 
     * This also takes into account if the visible rows are at the very start or
     * end of the table, if the user scrolled or the
     * Table.setCurrentPageFirstItemIndex(int) method was used.
     * 
     * This value should not change if cache_rate and pageLength are not changed
     * as well, and if this test remains constant: the table is scrolled to the
     * very end (done in the actual UI: SetPageFirstItemLoadsNeededRowsOnly).
     */
    private int expectedRowsRequested = 45;

    @Test
    public void verifyLoadedRows() throws InterruptedException {

        openTestURL();

        // wait for events to be processed in UI after loading page
        sleep(2000);

        String labelValue = $(LabelElement.class).get(1).getText();
        String expectedLabelValue = "rows requested: " + expectedRowsRequested;
        String errorMessage = "Too many rows were requested";
        assertEquals(errorMessage, expectedLabelValue, labelValue);
    }
}
