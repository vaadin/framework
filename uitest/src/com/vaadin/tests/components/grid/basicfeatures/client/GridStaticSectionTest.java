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

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeatures;

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

            if (i % 3 == 0) {
                assertText(String.format("Header (%d,%d)", headerId, i), cell);
            } else if (i % 2 == 0) {
                assertHTML(String.format("<b>Header (%d,%d)</b>", headerId, i),
                        cell);
            } else {
                assertHTML(String.format(
                        "<div class=\"gwt-HTML\">Header (%d,%d)</div>",
                        headerId, i), cell);
            }

            i++;
        }
        assertEquals("number of header columns", GridBasicFeatures.COLUMNS, i);
    }

    protected void assertFooterTexts(int footerId, int rowIndex) {
        int i = 0;
        for (TestBenchElement cell : getGridElement().getFooterCells(rowIndex)) {
            if (i % 3 == 0) {
                assertText(String.format("Footer (%d,%d)", footerId, i), cell);
            } else if (i % 2 == 0) {
                assertHTML(String.format("<b>Footer (%d,%d)</b>", footerId, i),
                        cell);
            } else {
                assertHTML(String.format(
                        "<div class=\"gwt-HTML\">Footer (%d,%d)</div>",
                        footerId, i), cell);
            }
            i++;
        }
        assertEquals("number of footer columns", GridBasicFeatures.COLUMNS, i);
    }

    protected static void assertText(String text, TestBenchElement e) {
        // TBE.getText returns "" if the element is scrolled out of view
        assertEquals(text, e.getAttribute("innerHTML"));
    }

    protected static void assertHTML(String text, TestBenchElement e) {
        String html = e.getAttribute("innerHTML");

        // IE 8 returns tags as upper case while other browsers do not, make the
        // comparison non-casesensive
        html = html.toLowerCase();
        text = text.toLowerCase();

        // IE 8 returns attributes without quotes, make the comparison without
        // quotes
        html = html.replaceAll("\"", "");
        text = html.replaceAll("\"", "");

        assertEquals(text, html);
    }
}
