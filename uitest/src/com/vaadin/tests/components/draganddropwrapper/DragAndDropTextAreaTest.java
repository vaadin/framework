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
package com.vaadin.tests.components.draganddropwrapper;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for drag image of text area which should contain text-area text.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class DragAndDropTextAreaTest extends MultiBrowserTest {

    @Test
    public void testTextAreaDndImage() {
        openTestURL();

        WebElement wrapper = driver.findElement(By
                .className("v-verticallayout"));
        Actions actions = new Actions(driver);
        actions.clickAndHold(wrapper);
        actions.moveByOffset(50, 50);
        actions.perform();

        WebElement dragElement = driver.findElement(By
                .className("v-drag-element"));
        List<WebElement> children = dragElement.findElements(By.xpath(".//*"));
        boolean found = false;
        for (WebElement child : children) {
            if ("text".equals(child.getAttribute("value"))) {
                found = true;
            }
        }

        Assert.assertTrue(
                "Text value is not found in the DnD image of text area", found);
    }

}
