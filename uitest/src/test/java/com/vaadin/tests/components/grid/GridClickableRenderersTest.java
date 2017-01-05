/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.components.grid;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridClickableRenderersTest extends MultiBrowserTest {

    @Test
    public void clickableRenderersPresent() {
        openTestURL();
        Assert.assertTrue(isElementPresent(By.className("v-nativebutton")));
        Assert.assertTrue(isElementPresent(By.className("gwt-Image")));
    }

    @Test
    public void buttonRendererReturnsCorrectItem() {
        openTestURL();
        WebElement firstRowButton = findElements(By.className("v-nativebutton"))
                .get(0);
        WebElement secondRowButton = findElements(
                By.className("v-nativebutton")).get(2);
        LabelElement label = $(LabelElement.class).get(1);

        firstRowButton.click();
        Assert.assertEquals("first row clicked", label.getText());

        secondRowButton.click();
        Assert.assertEquals("second row clicked", label.getText());
    }

    @Test
    public void checkBoxRendererClick() {
        openTestURL();
        WebElement firstRowButton = findElements(By.className("v-nativebutton"))
                .get(1);
        WebElement secondRowButton = findElements(
                By.className("v-nativebutton")).get(3);
        LabelElement label = $(LabelElement.class).get(2);

        firstRowButton.click();
        Assert.assertEquals("first row false", label.getText());

        secondRowButton.click();
        Assert.assertEquals("second row true", label.getText());
    }
}
