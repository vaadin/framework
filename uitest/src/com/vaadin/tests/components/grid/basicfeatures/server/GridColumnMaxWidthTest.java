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

import org.junit.Test;

import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class GridColumnMaxWidthTest extends GridBasicFeaturesTest {

    @Test
    public void testMaxWidthAffectsColumnWidth() {
        setDebug(true);
        openTestURL();

        selectMenuPath("Component", "Columns",
                "All columns expanding, Col 0 has max width of 30px");

        assertEquals("Column 0 did not obey max width of 30px.", 30,
                getGridElement().getCell(0, 0).getSize().getWidth());
    }
}