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
package com.vaadin.tests.components.datefield;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * 
 * @author Vaadin Ltd
 */
public class DisabledParentLayoutTest extends MultiBrowserTest {

    @Test
    public void testEnableParentLayout() {
        openTestURL();

        WebElement button = driver.findElement(By.className("v-button"));
        button.click();

        WebElement textField = driver.findElement(By
                .className("v-datefield-textfield"));
        textField.click();

        Assert.assertFalse(
                "Date input text field shoud be disabled for disabled DateField",
                textField.isEnabled());

        WebElement dataFieldButton = driver.findElement(By
                .className("v-datefield-button"));
        dataFieldButton.click();

        Assert.assertFalse(
                "Disabled date popup is opened after click to its button",
                isElementPresent(By.className("v-datefield-popup")));

        button.click();

        Assert.assertTrue(
                "Date input text field shoud be enabled for enabled DateField",
                textField.isEnabled());

        textField.click();
        String text = "text";
        textField.sendKeys(text);

        Assert.assertEquals("Unexpected text in date text field", text,
                textField.getAttribute("value"));

        dataFieldButton.click();
        Assert.assertFalse("Unexpected disabled element found",
                isElementPresent(By.className("v-disabled")));

        Assert.assertTrue("Date popup is not opened after click to its button",
                isElementPresent(By.className("v-datefield-popup")));
    }

}
