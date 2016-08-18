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

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elementsbase.AbstractElement;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.Table")
public class CustomTableElement extends TableElement {

    public CollapseMenu openCollapseMenu() {
        getCollapseMenuToggle().click();
        WebElement cm = getDriver()
                .findElement(By.xpath("//*[@id='PID_VAADIN_CM']"));
        return wrapElement(cm, getCommandExecutor()).wrap(CollapseMenu.class);
    }

    public static class CollapseMenu extends ContextMenuElement {
    }

    public WebElement getCollapseMenuToggle() {
        return findElement(By.className("v-table-column-selector"));
    }

    public static class ContextMenuElement extends AbstractElement {

        public WebElement getItem(int index) {
            return findElement(
                    By.xpath(".//table//tr[" + (index + 1) + "]//td/*"));
        }

    }

    public ContextMenuElement getContextMenu() {
        WebElement cm = getDriver().findElement(By.className("v-contextmenu"));
        return wrapElement(cm, getCommandExecutor())
                .wrap(ContextMenuElement.class);
    }

}
