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
package com.vaadin.tests.components.optiongroup;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for Option group (with new items allowed): unset read only state.
 * 
 * @author Vaadin Ltd
 */
public class ReadOnlyOptionGroupTest extends MultiBrowserTest {

    @Test
    public void testOptionGroup() {
        setDebug(true);
        openTestURL();

        WebElement checkbox = driver.findElement(By.className("v-checkbox"));
        WebElement checkboxInput = checkbox.findElement(By.tagName("input"));
        checkboxInput.click();

        Assert.assertFalse("There is a client side exception after unset "
                + "readonly mode for option group",
                isElementPresent(By.className("v-Notification-error")));

        Assert.assertFalse("Radio button in option group is still disabled "
                + "after unset reaonly",
                isElementPresent(By.className("v-radiobutton-disabled")));
    }

}
