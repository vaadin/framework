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
package com.vaadin.tests.components.table;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for mouse details in AbstractSelectTargetDetails class when DnD target
 * is a table.
 * 
 * @since 7.3
 * @author Vaadin Ltd
 */
public class DndTableTargetDetailsTest extends MultiBrowserTest {

    @Test
    public void testMouseDetails() throws IOException, InterruptedException {
        openTestURL();

        WebElement row = driver.findElement(By
                .className("v-table-cell-wrapper"));

        Actions actions = new Actions(driver);
        actions.moveToElement(row);
        pressKeys(actions);
        actions.clickAndHold();
        actions.release(getTarget());
        actions.build().perform();

        WebElement label = driver.findElement(By.className("dnd-button-name"));
        Assert.assertEquals("Button name=left", label.getText());
        checkPressedKeys();
    }

    protected WebElement getTarget() {
        return driver.findElement(By.className("target")).findElement(
                By.className("v-table-row-spacer"));
    }

    protected void pressKeys(Actions actions) {
        actions.keyDown(Keys.CONTROL);
    }

    protected void checkPressedKeys() {
        Assert.assertTrue(isElementPresent(By.className("ctrl")));
    }

}
