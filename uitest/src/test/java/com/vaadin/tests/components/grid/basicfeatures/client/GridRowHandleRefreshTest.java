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
package com.vaadin.tests.components.grid.basicfeatures.client;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;

public class GridRowHandleRefreshTest extends GridBasicClientFeaturesTest {

    @Test
    public void testRefreshingThroughRowHandle() {
        openTestURL();

        assertEquals("Unexpected initial state", "(0, 0)", getGridElement()
                .getCell(0, 0).getText());
        selectMenuPath("Component", "State", "Edit and refresh Row 0");
        assertEquals("Cell contents did not update correctly", "Foo",
                getGridElement().getCell(0, 0).getText());
    }

    @Test
    public void testDelayedRefreshingThroughRowHandle()
            throws InterruptedException {
        openTestURL();

        assertEquals("Unexpected initial state", "(0, 0)", getGridElement()
                .getCell(0, 0).getText());
        selectMenuPath("Component", "State", "Delayed edit of Row 0");
        // Still the same data
        assertEquals("Cell contents did not update correctly", "(0, 0)",
                getGridElement().getCell(0, 0).getText());
        sleep(5000);
        // Data should be updated
        assertEquals("Cell contents did not update correctly", "Bar",
                getGridElement().getCell(0, 0).getText());
    }

    @Test
    public void testRefreshingWhenNotInViewThroughRowHandle() {
        openTestURL();

        assertEquals("Unexpected initial state", "(0, 0)", getGridElement()
                .getCell(0, 0).getText());
        getGridElement().scrollToRow(100);
        selectMenuPath("Component", "State", "Edit and refresh Row 0");
        assertEquals("Cell contents did not update correctly", "Foo",
                getGridElement().getCell(0, 0).getText());
    }
}
