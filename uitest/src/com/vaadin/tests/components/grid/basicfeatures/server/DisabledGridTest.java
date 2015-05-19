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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class DisabledGridTest extends GridBasicFeaturesTest {

    @Before
    public void setUp() {
        openTestURL();
        selectMenuPath("Component", "State", "Enabled");
    }

    @Test
    public void testSelection() {
        selectMenuPath("Component", "State", "Selection mode", "single");

        GridRowElement row = getGridElement().getRow(0);
        GridCellElement cell = getGridElement().getCell(0, 0);
        cell.click();
        assertFalse("disabled row should not be selected", row.isSelected());

    }

    @Test
    public void testEditorOpening() {
        selectMenuPath("Component", "Editor", "Enabled");

        GridRowElement row = getGridElement().getRow(0);
        GridCellElement cell = getGridElement().getCell(0, 0);
        cell.click();
        assertNull("Editor should not open", getEditor());

        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();
        assertNull("Editor should not open", getEditor());
    }
}
