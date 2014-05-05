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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class VerticalLayoutFocusWithDOMChangesTest extends MultiBrowserTest {

    private String initialText = "Some";
    private String incrementalText = " text";

    @Test
    public void inputTextAndChangeFocus() throws InterruptedException {
        openTestURL();
        List<WebElement> textFields = getDriver().findElements(
                By.tagName("input"));
        WebElement tf1 = textFields.get(0);
        WebElement tf2 = textFields.get(1);
        tf1.sendKeys(initialText);
        new Actions(getDriver()).moveToElement(tf2).click().build().perform();

        WebElement activeElement = getFocusedElement();
        Assert.assertEquals("input", activeElement.getTagName());
        Assert.assertEquals("", activeElement.getAttribute("value"));

        tf1.sendKeys(incrementalText);
        new Actions(getDriver())
                .moveToElement(
                        getDriver().findElement(By.className("v-button")))
                .click().build().perform();
        activeElement = getFocusedElement();
        Assert.assertEquals("Just a button", activeElement.getText());

        DesiredCapabilities capabilities = getDesiredCapabilities();
        if (capabilities.equals(BrowserUtil.ie(8))
                || capabilities.equals(BrowserUtil.ie(9))) {
            // IE8 and IE9 insert cursor in the start of input instead of end.
            Assert.assertEquals(incrementalText + initialText,
                    tf1.getAttribute("value"));
        } else {
            Assert.assertEquals(initialText + incrementalText,
                    tf1.getAttribute("value"));
        }
    }

    @Test
    public void moveFocusAndChangeFieldWithValue() {
        openTestURL();
        List<WebElement> textFields = getDriver().findElements(
                By.tagName("input"));
        WebElement tf1 = textFields.get(0);
        WebElement tf2 = textFields.get(1);

        String firstText = "This is";
        String secondText = " default value";

        tf2.sendKeys(firstText);
        tf1.sendKeys(initialText);
        new Actions(getDriver()).moveToElement(tf2).click().build().perform();

        WebElement activeElement = getFocusedElement();
        Assert.assertEquals("input", activeElement.getTagName());
        Assert.assertEquals(firstText, activeElement.getAttribute("value"));

        new Actions(getDriver()).sendKeys(secondText).build().perform();
        DesiredCapabilities capabilities = getDesiredCapabilities();
        if (capabilities.equals(BrowserUtil.ie(8))
                || capabilities.equals(BrowserUtil.ie(9))) {
            // IE8 and IE9 insert cursor in the start of input instead of end.
            Assert.assertEquals(secondText + firstText,
                    tf2.getAttribute("value"));
        } else {
            Assert.assertEquals(firstText + secondText,
                    tf2.getAttribute("value"));
        }
    }

}
