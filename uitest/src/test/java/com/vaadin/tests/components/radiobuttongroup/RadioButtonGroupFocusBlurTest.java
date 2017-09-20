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
package com.vaadin.tests.components.radiobuttongroup;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 *
 */
public class RadioButtonGroupFocusBlurTest extends MultiBrowserTest {

    @Test
    public void focusBlurEvents() {
        openTestURL();

        RadioButtonGroupElement radioButtonGroup = $(
                RadioButtonGroupElement.class).first();
        List<WebElement> radioButtons = radioButtonGroup
                .findElements(By.tagName("input"));
        radioButtonGroup.selectByText("1");

        // Focus event is fired
        Assert.assertTrue(logContainsText("1. Focus Event"));

        radioButtonGroup.selectByText("2");
        // click on the second radio button doesn't fire anything
        Assert.assertFalse(logContainsText("2."));

        // move the cursor to the middle of the first element,
        // offset to the middle of the two and perform click
        new Actions(getDriver()).moveToElement(radioButtons.get(0))
                .moveByOffset(0,
                        (radioButtons.get(1).getLocation().y
                                - radioButtons.get(0).getLocation().y) / 2)
                .click().build().perform();
        // no new events
        Assert.assertFalse(logContainsText("2."));

        // click to label of a radio button
        radioButtonGroup.findElements(By.tagName("label")).get(2).click();
        // no new events
        Assert.assertFalse(logContainsText("2."));

        // click on log label => blur
        $(LabelElement.class).first().click();
        // blur event is fired
        Assert.assertTrue(logContainsText("2. Blur Event"));

        radioButtonGroup.selectByText("4");
        // Focus event is fired
        Assert.assertTrue(logContainsText("3. Focus Event"));

        // move keyboard focus to the next radio button
        radioButtons.get(3).sendKeys(Keys.ARROW_DOWN);
        // no new events
        Assert.assertFalse(logContainsText("4."));

        // select the next radio button
        radioButtons.get(4).sendKeys(Keys.TAB);
        // focus has gone away
        waitUntil(driver -> logContainsText("4. Blur Event"), 5);
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Focus does not move when expected with Selenium/TB and Firefox 45
        return getBrowsersExcludingFirefox();
    }
}
