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

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.ui.DragAndDropWrapper;

/**
 * Test for text area inside {@link DragAndDropWrapper}: text area should obtain
 * focus on click.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class DragAndDropFocusObtainTest extends MultiBrowserTest {

    @Test
    public void testTextAreaDndImage() {
        openTestURL();

        WebElement wrapper = driver.findElement(By.className("v-ddwrapper"));
        Actions actions = new Actions(driver);
        actions.click(wrapper);
        actions.perform();

        WebElement focusedElement = driver.findElement(By
                .className("v-textarea-focus"));
        Assert.assertNotNull("Text area did not obtain focus after click",
                focusedElement);

    }

}
