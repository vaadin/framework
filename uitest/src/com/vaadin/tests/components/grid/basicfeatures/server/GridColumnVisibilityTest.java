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
package com.vaadin.tests.components.grid.basicfeatures.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

@TestCategory("grid")
@RunLocally(Browser.PHANTOMJS)
public class GridColumnVisibilityTest extends GridBasicFeaturesTest {

    private static final String[] TOGGLE_LISTENER = new String[] { "Component",
            "State", "ColumnVisibilityChangeListener" };
    private static final String[] TOGGLE_HIDE_COLUMN_0 = new String[] {
            "Component", "Columns", "Column 0", "Hidden" };

    private static final String COLUMN_0_BECAME_HIDDEN_MSG = "Visibility "
            + "changed: propertyId: Column 0, isHidden: true";
    private static final String COLUMN_0_BECAME_UNHIDDEN_MSG = "Visibility "
            + "changed: propertyId: Column 0, isHidden: false";

    @Before
    public void setUp() {
        openTestURL();
    }

    @Test
    public void columnIsNotShownWhenHidden() {
        assertEquals("column 0", getGridElement().getHeaderCell(0, 0).getText()
                .toLowerCase());

        selectMenuPath(TOGGLE_HIDE_COLUMN_0);
        assertEquals("column 1", getGridElement().getHeaderCell(0, 0).getText()
                .toLowerCase());
    }

    @Test
    public void columnIsShownWhenUnhidden() {
        selectMenuPath(TOGGLE_HIDE_COLUMN_0);
        selectMenuPath(TOGGLE_HIDE_COLUMN_0);
        assertEquals("column 0", getGridElement().getHeaderCell(0, 0).getText()
                .toLowerCase());
    }

    @Test
    public void registeringListener() {
        assertFalse(logContainsText(COLUMN_0_BECAME_HIDDEN_MSG));
        selectMenuPath(TOGGLE_LISTENER);
        assertFalse(logContainsText(COLUMN_0_BECAME_HIDDEN_MSG));

        selectMenuPath(TOGGLE_HIDE_COLUMN_0);
        assertTrue(logContainsText(COLUMN_0_BECAME_HIDDEN_MSG));

        selectMenuPath(TOGGLE_HIDE_COLUMN_0);
        assertTrue(logContainsText(COLUMN_0_BECAME_UNHIDDEN_MSG));
    }

    @Test
    public void deregisteringListener() {
        selectMenuPath(TOGGLE_LISTENER);
        selectMenuPath(TOGGLE_HIDE_COLUMN_0);

        selectMenuPath(TOGGLE_LISTENER);
        selectMenuPath(TOGGLE_HIDE_COLUMN_0);
        assertFalse(logContainsText(COLUMN_0_BECAME_UNHIDDEN_MSG));
    }
}
