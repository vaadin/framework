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

import static org.junit.Assert.assertEquals;

import com.vaadin.testbench.TestBenchElement;

/**
 * Abstract base class for header and footer tests.
 * 
 * @since
 * @author Vaadin Ltd
 */
public abstract class GridStaticSectionTest extends GridBasicClientFeaturesTest {

    protected void assertHeaderTexts(int headerId, int rowIndex) {
        int i = 0;
        for (TestBenchElement cell : getGridElement().getHeaderCells(rowIndex)) {
            assertText(String.format("Header (%d,%d)", headerId, i), cell);
            i++;
        }
        assertEquals("number of header columns", GridBasicFeatures.COLUMNS, i);
    }

    protected void assertFooterTexts(int footerId, int rowIndex) {
        int i = 0;
        for (TestBenchElement cell : getGridElement().getFooterCells(rowIndex)) {
            assertText(String.format("Footer (%d,%d)", footerId, i), cell);
            i++;
        }
        assertEquals("number of footer columns", GridBasicFeatures.COLUMNS, i);
    }

    protected static void assertText(String text, TestBenchElement e) {
        // TBE.getText returns "" if the element is scrolled out of view
        assertEquals(text, e.getAttribute("innerHTML"));
    }
}
