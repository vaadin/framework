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
package com.vaadin.tests.components.formlayout;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.FormLayoutElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for form layout click listener.
 *
 * @author Vaadin Ltd
 */
public class FormLayoutClickListenerTest extends MultiBrowserTest {

    @Before
    public void setUp() {
        openTestURL();
        waitForElementPresent(By.id("label"));
    }

    @Test
    public void layoutClickListener_clickOnLayout_childAndClickedComponentsAreNull() {
        FormLayoutElement element = $(FormLayoutElement.class).first();
        Actions actions = new Actions(getDriver());
        actions.moveByOffset(element.getLocation().getX() + 2,
                element.getLocation().getY() + 2).click().build().perform();
        waitForLogRowUpdate();

        Assert.assertEquals("Source component for click event must be form",
                "3. Source component: form", getLogRow(0));
        Assert.assertEquals("Clicked component for click event must be null",
                "2. Clicked component: null", getLogRow(1));
        Assert.assertEquals("Child component for click event must be null",
                "1. Child component: null", getLogRow(2));
    }

    @Test
    public void layoutClickListener_clickOnLabel_lableIsChildAndClickedComponent() {
        findElement(By.id("label")).click();
        waitForLogRowUpdate();

        Assert.assertEquals("Source component for click event must be form",
                "3. Source component: form", getLogRow(0));
        Assert.assertEquals("Clicked component for click event must be label",
                "2. Clicked component: label", getLogRow(1));
        Assert.assertEquals("Child component for click event must be label",
                "1. Child component: label", getLogRow(2));
    }

    private void waitForLogRowUpdate() {
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return !getLogRow(2).trim().isEmpty();
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return "log rows to be updated";
            }
        });
    }

}
