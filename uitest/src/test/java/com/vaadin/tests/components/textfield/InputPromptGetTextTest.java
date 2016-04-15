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
package com.vaadin.tests.components.textfield;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class InputPromptGetTextTest extends MultiBrowserTest {

    @Test
    public void test() {
        openTestURL();

        WebElement field = getDriver().findElement(
                By.id(InputPromptGetText.FIELD));

        WebElement button = getDriver().findElement(
                By.id(InputPromptGetText.BUTTON));

        String string = getRandomString();
        field.sendKeys(string + "\n");

        String selectAll = Keys.chord(Keys.CONTROL, "a");
        field.sendKeys(selectAll);
        field.sendKeys(Keys.BACK_SPACE);

        button.click();

        WebElement label = getDriver().findElement(
                By.id(InputPromptGetText.LABEL2));

        Assert.assertEquals("Your input was:", label.getText().trim());
    }

    private String getRandomString() {
        String string = RandomStringUtils.randomAlphanumeric(3);
        return string;
    }

}
