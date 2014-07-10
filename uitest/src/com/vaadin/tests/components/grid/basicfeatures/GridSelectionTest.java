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

import org.junit.Test;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class GridSelectionTest extends GridBasicFeaturesTest {

    @Test
    public void testSelectOnOff() throws Exception {
        openTestURL();

        setSelectionModelMulti();

        assertFalse("row shouldn't start out as selected",
                isSelected(getRow(0)));
        toggleFirstRowSelection();
        assertTrue("row should become selected", isSelected(getRow(0)));
        toggleFirstRowSelection();
        assertFalse("row shouldn't remain selected", isSelected(getRow(0)));
    }

    @Test
    public void testSelectOnScrollOffScroll() throws Exception {
        openTestURL();

        setSelectionModelMulti();

        assertFalse("row shouldn't start out as selected",
                isSelected(getRow(0)));
        toggleFirstRowSelection();
        assertTrue("row should become selected", isSelected(getRow(0)));

        scrollGridVerticallyTo(10000); // make sure the row is out of cache
        scrollGridVerticallyTo(0); // scroll it back into view

        assertTrue("row should still be selected when scrolling "
                + "back into view", isSelected(getRow(0)));
    }

    @Test
    public void testSelectScrollOnScrollOff() throws Exception {
        openTestURL();

        setSelectionModelMulti();

        assertFalse("row shouldn't start out as selected",
                isSelected(getRow(0)));

        scrollGridVerticallyTo(10000); // make sure the row is out of cache
        toggleFirstRowSelection();

        scrollGridVerticallyTo(0); // scroll it back into view
        assertTrue("row should still be selected when scrolling "
                + "back into view", isSelected(getRow(0)));

        toggleFirstRowSelection();
        assertFalse("row shouldn't remain selected", isSelected(getRow(0)));
    }

    @Test
    public void testSelectScrollOnOffScroll() throws Exception {
        openTestURL();

        setSelectionModelMulti();

        assertFalse("row shouldn't start out as selected",
                isSelected(getRow(0)));

        scrollGridVerticallyTo(10000); // make sure the row is out of cache
        toggleFirstRowSelection();
        toggleFirstRowSelection();

        scrollGridVerticallyTo(0); // make sure the row is out of cache
        assertFalse("row shouldn't be selected when scrolling "
                + "back into view", isSelected(getRow(0)));
    }

    private void setSelectionModelMulti() {
        selectMenuPath("Component", "State", "Selection mode", "multi");
    }

    @SuppressWarnings("static-method")
    private boolean isSelected(TestBenchElement row) {
        /*
         * FIXME We probably should get a GridRow instead of a plain
         * TestBenchElement, that has an "isSelected" thing integrated. (henrik
         * paul 26.6.2014)
         */
        return row.getAttribute("class").contains("-row-selected");
    }

    private void toggleFirstRowSelection() {
        selectMenuPath("Component", "Body rows", "Select first row");
    }

    private TestBenchElement getRow(int i) {
        return getGridElement().getRow(i);
    }
}
