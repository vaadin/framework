/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.focusable;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.components.grid.basics.GridBasics;

public class GridFocusableTest extends AbstractFocusableComponentTest {

    @Override
    protected Class<?> getUIClass() {
        return GridBasics.class;
    }

    @Override
    protected String getTabIndex() {
        return $(GridElement.class).first().getAttribute("tabindex");
    }

    @Override
    protected boolean isFocused() {
        return getFocusElement().isFocused();
    }

    @Override
    protected GridCellElement getFocusElement() {
        return $(GridElement.class).first().getCell(0, 0);
    }

    @Override
    protected WebElement getMenuElement(String menuCaption)
            throws NoSuchElementException {
        return super.getMenuElement(menuCaption).findElement(By.xpath(".."));
    }
}
