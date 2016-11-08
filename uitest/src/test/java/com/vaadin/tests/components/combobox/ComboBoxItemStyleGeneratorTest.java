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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.SingleBrowserTest;
import com.vaadin.tests.tb3.newelements.ComboBoxElement;

public class ComboBoxItemStyleGeneratorTest extends SingleBrowserTest {
    @Test
    public void testItemStyleGenerator() {
        openTestURL();

        ComboBoxElement comboBox = $(ComboBoxElement.class).first();

        selectMenuPath("Component", "Features", "Item style generator",
                "Bold fives");

        comboBox.openPopup();

        List<WebElement> boldItems = findElements(
                By.className("v-filterselect-item-bold"));

        Assert.assertEquals(1, boldItems.size());
        Assert.assertEquals("Item 5", boldItems.get(0).getText());

        selectMenuPath("Component", "Features", "Item style generator", "-");

        boldItems = findElements(By.className("v-filterselect-item-bold"));
        Assert.assertEquals(0, boldItems.size());
    }

    @Override
    protected Class<?> getUIClass() {
        return ComboBoxes2.class;
    }

}
