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

import org.junit.Test;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class BasicCrudGridEditorRowTest extends MultiBrowserTest {

    @Test
    public void lookAndFeel() throws Exception {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        GridCellElement ritaBirthdate = grid.getCell(2, 3);
        compareScreen("grid");

        // Open editor row
        new Actions(getDriver()).doubleClick(ritaBirthdate).perform();
        compareScreen("editorrow");

    }
}
