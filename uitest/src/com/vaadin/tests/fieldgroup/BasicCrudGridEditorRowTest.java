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
package com.vaadin.tests.fieldgroup;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridEditorElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class BasicCrudGridEditorRowTest extends MultiBrowserTest {
    private GridElement grid;

    @Before
    public void openTest() {
        openTestURL();
        grid = $(GridElement.class).first();

    }

    @Test
    public void lookAndFeel() throws Exception {
        GridCellElement ritaBirthdate = grid.getCell(2, 3);
        compareScreen("grid");

        // Open editor row
        new Actions(getDriver()).doubleClick(ritaBirthdate).perform();
        compareScreen("editorrow");
    }

    @Test
    public void editorRowOneInvalidValue() throws Exception {
        GridCellElement ritaBirthdate = grid.getCell(2, 3);
        // Open editor row
        new Actions(getDriver()).doubleClick(ritaBirthdate).perform();

        GridEditorElement editor = grid.getEditor();
        DateFieldElement dateField = editor.$(DateFieldElement.class).first();
        WebElement input = dateField.findElement(By.xpath("input"));
        // input.click();
        input.sendKeys("Invalid", Keys.TAB);
        editor.save();

        Assert.assertTrue("Editor wasn't displayed.", editor.isDisplayed());
        Assert.assertTrue("DateField wasn't displayed.",
                dateField.isDisplayed());

        Assert.assertTrue("DateField didn't have 'v-invalid' css class.",
                hasCssClass(dateField, "v-datefield-error"));
    }

}
