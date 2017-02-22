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

import java.util.List;

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
        List<WebElement> findElements = findElements(
                By.className("v-nativebutton"));
        WebElement firstRowTextButton = findElements.get(0);
        WebElement firstRowHtmlButton = findElements.get(1);
        Assert.assertEquals("button 1 text", firstRowTextButton.getText());
        // If it was rendered as text, getText() would return the markup also
        Assert.assertEquals("button 1 html", firstRowHtmlButton.getText());

        WebElement secondRowTextButton = findElements.get(3);
        WebElement secondRowHtmlButton = findElements.get(4);
        Assert.assertEquals("button 2 text", secondRowTextButton.getText());
        // If it was rendered as text, getText() would return the markup also
        Assert.assertEquals("button 2 html", secondRowHtmlButton.getText());

        LabelElement label = $(LabelElement.class).get(1);

        firstRowTextButton.click();
        Assert.assertEquals("first row clicked", label.getText());

        secondRowTextButton.click();
        Assert.assertEquals("second row clicked", label.getText());
    }

    @Test
    public void checkBoxRendererClick() {
        openTestURL();
        WebElement firstRowButton = findElements(By.className("v-nativebutton"))
                .get(2);
        WebElement secondRowButton = findElements(
                By.className("v-nativebutton")).get(5);
        LabelElement label = $(LabelElement.class).get(2);

        firstRowButton.click();
        Assert.assertEquals("first row false", label.getText());

        secondRowButton.click();
        Assert.assertEquals("second row true", label.getText());
    }
}
