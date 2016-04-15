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
package com.vaadin.tests.components.combobox;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to check whether combobox is expanded when icon is clicked.
 * 
 * @author Vaadin Ltd
 */
public class ComboBoxClickIconTest extends MultiBrowserTest {

    @Test
    public void testClickOnIconInCombobox() {
        openTestURL();

        $(ComboBoxElement.class).first().openPopup();

        getDriver().findElements(By.className("gwt-MenuItem")).get(1).click();

        getDriver().findElement(By.className("v-filterselect"))
                .findElement(By.className("v-icon")).click();

        Assert.assertTrue("Unable to find menu items in combobox popup",
                isElementPresent(By.className("gwt-MenuItem")));
    }

}
