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
package com.vaadin.tests.components.splitpanel;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class SplitPanelMoveComponentTest extends MultiBrowserTest {

    private static final String BUTTON_TEXT = "Button in splitpanel. Click to move to the other side";

    @Test
    public void moveComponent() {
        openTestURL();
        Assert.assertEquals(BUTTON_TEXT, getFirstChild().getText());
        getFirstChild().click();
        Assert.assertEquals(BUTTON_TEXT, getSecondChild().getText());
        getSecondChild().click();
        Assert.assertEquals(BUTTON_TEXT, getFirstChild().getText());
    }

    private WebElement getFirstChild() {
        WebElement container = getDriver()
                .findElement(
                        By.xpath("//div[contains(@class,'v-splitpanel-first-container')]"));
        return container.findElement(By
                .xpath("//div[contains(@class, 'v-button')]"));
    }

    private WebElement getSecondChild() {
        WebElement container = getDriver()
                .findElement(
                        By.xpath("//div[contains(@class,'v-splitpanel-second-container')]"));
        return container.findElement(By
                .xpath("//div[contains(@class, 'v-button')]"));
    }

}
