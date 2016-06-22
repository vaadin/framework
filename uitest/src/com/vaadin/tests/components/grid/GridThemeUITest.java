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
package com.vaadin.tests.components.grid;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridEditorElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserThemeTest;

@TestCategory("grid")
public class GridThemeUITest extends MultiBrowserThemeTest {

    private GridElement grid;

    @Test
    public void grid() throws Exception {
        openTestURL();
        selectPage("Editor");
        compareScreen("basic");
    }

    @Test
    public void headerAndFooter() throws Exception {
        openTestURL();
        selectPage("HeaderFooter");
        compareScreen("basic");
        grid.getHeaderCell(0, 6).$(ButtonElement.class).first().click();
        compareScreen("additional-header");
        grid.getHeaderCell(2, 1).click();
        compareScreen("sorted-last-name");
        grid.getHeaderCell(2, 4).click();
        compareScreen("sorted-age");
    }

    @Test
    public void editor() throws Exception {
        openTestURL();
        selectPage("Editor");
        GridCellElement ritaBirthdate = grid.getCell(2, 3);
        // Open editor row
        openEditor(ritaBirthdate);

        compareScreen("initial");

        GridEditorElement editor = grid.getEditor();

        DateFieldElement dateField = editor.$(DateFieldElement.class).first();
        WebElement input = dateField.findElement(By.xpath("input"));
        input.sendKeys("Invalid", Keys.TAB);
        editor.save();
        compareScreen("one-invalid");

        TextFieldElement age = editor.$(TextFieldElement.class).caption("Age")
                .first();
        age.sendKeys("abc", Keys.TAB);
        if (age.getValue().equals("21")) {
            // Yes IE8, really type into the field
            age.sendKeys("abc", Keys.TAB);
        }
        editor.save();

        compareScreen("two-invalid");
    }

    private void openEditor(GridCellElement targetCell) {
        new Actions(getDriver()).doubleClick(targetCell).perform();
        try {
            if (grid.getEditor().isDisplayed()) {
                return;
            }
        } catch (Exception e) {

        }

        // Try again if IE happen to fail..
        new Actions(getDriver()).doubleClick(targetCell).perform();
    }

    /**
     * @param string
     */
    private void selectPage(String string) {
        $(NativeSelectElement.class).id("page").selectByText(string);
        grid = $(GridElement.class).first();

    }

}
