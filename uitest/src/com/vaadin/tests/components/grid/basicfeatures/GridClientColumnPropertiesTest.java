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

import org.junit.Test;

import com.vaadin.tests.widgetset.client.grid.GridBasicClientFeatures;

public class GridClientColumnPropertiesTest extends GridBasicClientFeaturesTest {

    @Test
    public void initialColumnWidths() {
        openTestURL();

        for (int col = 0; col < GridBasicClientFeatures.COLUMNS; col++) {
            int width = getGridElement().getCell(0, col).getSize().getWidth();
            if (col <= 6) {
                // Growing column widths
                assertEquals(50 + col * 25, width);
            } else {
                assertEquals(100, width);
            }
        }
    }

    @Test
    public void testChangingColumnWidth() {
        openTestURL();

        selectMenuPath("Component", "Columns", "Column 0", "Width", "50px");
        int width = getGridElement().getCell(0, 0).getSize().getWidth();
        assertEquals(50, width);

        selectMenuPath("Component", "Columns", "Column 0", "Width", "200px");
        width = getGridElement().getCell(0, 0).getSize().getWidth();
        assertEquals(200, width);

        selectMenuPath("Component", "Columns", "Column 0", "Width", "auto");
        width = getGridElement().getCell(0, 0).getSize().getWidth();
        assertEquals(100, width);
    }

}
