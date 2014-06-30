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
package com.vaadin.tests.tooltip;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TooltipAndJavascriptTest extends MultiBrowserTest {

    @Test
    public void ensureTooltipInOverlay() throws InterruptedException {
        openTestURL();
        $(ButtonElement.class).first().showTooltip();
        WebElement tooltip = findElement(By
                .cssSelector(".v-overlay-container .v-tooltip"));
        WebElement overlayContainer = getParent(tooltip);
        Assert.assertTrue("v-overlay-container did not receive theme",
                hasClass(overlayContainer, "reindeer"));
    }

    private boolean hasClass(WebElement element, String classname) {
        String[] classes = element.getAttribute("class").split(" ");
        for (String classString : classes) {
            if (classname.equals(classString)) {
                return true;
            }
        }
        return false;
    }

    private WebElement getParent(WebElement element) {
        return (WebElement) ((JavascriptExecutor) getDriver()).executeScript(
                "return arguments[0].parentNode;", element);
    }
}
