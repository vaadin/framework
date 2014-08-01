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

import java.io.IOException;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;

public class GridStaticSectionComponentTest extends GridBasicFeaturesTest {

    @Test
    public void testNativeButtonInHeader() throws IOException {
        openTestURL();

        selectMenuPath("Component", "Columns", "Column 1", "Header Type",
                "Widget Header");

        getGridElement().$(ButtonElement.class).first().click();

        // Clicking also triggers sorting
        assertEquals("2. Button clicked!", getLogRow(2));

        compareScreen("button");
    }

    @Test
    public void testNativeButtonInFooter() throws IOException {
        openTestURL();

        selectMenuPath("Component", "Footer", "Visible");
        selectMenuPath("Component", "Footer", "Append row");
        selectMenuPath("Component", "Columns", "Column 1", "Footer Type",
                "Widget Footer");

        getGridElement().$(ButtonElement.class).first().click();

        assertEquals("4. Button clicked!", getLogRow(0));
    }
}
