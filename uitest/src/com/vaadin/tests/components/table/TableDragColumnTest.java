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
package com.vaadin.tests.components.table;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TableHeaderElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableDragColumnTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return Tables.class;
    }

    @Test
    public void testDragColumn() {
        openTestURL();

        selectMenuPath("Component", "Columns", "Property 1", "Icon", "ok 16x16");

        TableElement table = $(TableElement.class).first();
        TableHeaderElement dragged = table.getHeaderCell(0);
        String imgSrc = dragged.findElement(By.tagName("img")).getAttribute(
                "src");
        new Actions(getDriver()).moveToElement(dragged).clickAndHold()
                .moveByOffset(-6, 0).perform();

        Assert.assertTrue("No drag element visible",
                isElementPresent(By.className("v-table-header-drag")));

        WebElement dragImage = findElement(By.className("v-table-header-drag"));
        String cellContent = dragged.getText();
        Assert.assertEquals(
                "Drag image had different content than header cell",
                cellContent, dragImage.getText());
        Assert.assertEquals("Drag image had different icon", imgSrc, dragImage
                .findElement(By.tagName("img")).getAttribute("src"));

        TableHeaderElement target = table.getHeaderCell(3);
        new Actions(getDriver())
                .moveToElement(target, target.getSize().width - 5, 10)
                .release().perform();

        dragged = table.getHeaderCell(3);
        Assert.assertEquals("Column was not dropped where expected.",
                cellContent, dragged.getText());
        Assert.assertEquals("Drag image had different icon", imgSrc, dragged
                .findElement(By.tagName("img")).getAttribute("src"));
    }

    @Test
    public void testDragColumnFloatingElementStyle() {
        openTestURL();

        dragAndAssertStyleName("v-table-header-drag");

        selectMenuPath("Component", "Decorations", "Style name",
                "1px red border (border-red-1px)");
        dragAndAssertStyleName("border-red-1px");

        selectMenuPath("Component", "Decorations", "Style name",
                "2px blue border (border-blue-2px)");
        dragAndAssertStyleName("border-blue-2px");
    }

    private void dragAndAssertStyleName(String styleName) {
        TableHeaderElement dragged = $(TableElement.class).first()
                .getHeaderCell(0);
        new Actions(getDriver()).moveToElement(dragged).clickAndHold()
                .moveByOffset(-6, 0).perform();

        Assert.assertTrue("No drag element visible",
                isElementPresent(By.className("v-table-header-drag")));

        WebElement dragImage = findElement(By.className("v-table-header-drag"));
        String cellContent = dragged.getText();
        Assert.assertEquals(
                "Drag image had different content than header cell",
                cellContent, dragImage.getText());
        Assert.assertTrue("Missing CSS class " + styleName,
                hasCssClass(dragImage, styleName));

        new Actions(getDriver()).release().perform();
    }
}
