/*
 * Copyright 2000-2013 Vaadin Ltd.
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
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * 
 * @since 7.1
 * @author Vaadin Ltd
 */
public class DisabledDateFieldPopupTest extends MultiBrowserTest {

    @Test
    public void testPopup() {
        openTestURL();

        WebElement button = driver.findElement(By
                .className("v-datefield-button"));
        button.click();

        Assert.assertFalse(
                "Calendar popup should not be opened for disabled date field on mouse click",
                isElementPresent(By.className("v-datefield-popup")));

        button.sendKeys(Keys.ARROW_DOWN);

        Assert.assertFalse("Calendar popup should not be opened for "
                + "disabled date fild on down key",
                isElementPresent(By.className("v-datefield-popup")));

    }
}
