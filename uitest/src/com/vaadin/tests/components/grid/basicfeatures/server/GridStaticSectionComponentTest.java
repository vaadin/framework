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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class GridStaticSectionComponentTest extends GridBasicFeaturesTest {

    @Test
    public void testNativeButtonInHeader() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Columns", "Column 1", "Header Type",
                "Widget Header");

        getGridElement().$(ButtonElement.class).first().click();

        assertTrue("Button click should be logged",
                logContainsText("Button clicked!"));
    }

    @Test
    public void testNativeButtonInFooter() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Footer", "Visible");
        selectMenuPath("Component", "Footer", "Append row");
        selectMenuPath("Component", "Columns", "Column 1", "Footer Type",
                "Widget Footer");

        getGridElement().$(ButtonElement.class).first().click();

        assertTrue("Button click should be logged",
                logContainsText("Button clicked!"));
    }

    @Test
    public void testRemoveComponentFromHeader() throws Exception {
        openTestURL();
        selectMenuPath("Component", "Columns", "Column 1", "Header Type",
                "Widget Header");
        selectMenuPath("Component", "Columns", "Column 1", "Header Type",
                "Text Header");
        assertTrue("No notifications should've been shown",
                !$(NotificationElement.class).exists());
        assertEquals("Header should've been reverted back to text header",
                "text header", getGridElement().getHeaderCell(0, 1).getText()
                        .toLowerCase());
    }

}
