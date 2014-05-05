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
package com.vaadin.tests.components.orderedlayout;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class HorizontalLayoutFullsizeContentWithErrorMsgTest extends
        MultiBrowserTest {

    @Test
    public void test() {
        openTestURL();
        WebElement element = getDriver().findElement(
                By.id(HorizontalLayoutFullsizeContentWithErrorMsg.FIELD_ID));
        Point location = element.getLocation();

        WebElement errorToggleButton = getDriver().findElement(
                By.id(HorizontalLayoutFullsizeContentWithErrorMsg.BUTTON_ID));

        errorToggleButton.click();

        Assert.assertEquals(location, element.getLocation());

        errorToggleButton.click();

        Assert.assertEquals(location, element.getLocation());

    }

}
