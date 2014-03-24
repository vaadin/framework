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
package com.vaadin.tests.components.ui;

import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TooltipConfigurationTest extends MultiBrowserTest {

    private org.openqa.selenium.By tooltipBy = By
            .vaadin("Root/VTooltip[0]/FlowPanel[0]/domChild[1]");

    @Test
    public void testTooltipConfiguration() throws Exception {
        openTestURL();

        WebElement uiRoot = getDriver().findElement(By.vaadin("Root"));
        WebElement closeTimeout = vaadinElementById("Close timeout");
        WebElement shortTooltip = vaadinElementById("shortTooltip");
        WebElement longTooltip = vaadinElementById("longTooltip");
        WebElement maxWidth = vaadinElementById("Max width");

        selectAndType(closeTimeout, "0");
        testBenchElement(shortTooltip).showTooltip();
        waitForElementToBePresent(tooltipBy);
        Assert.assertEquals("This is a short tooltip", getTooltip().getText());

        new Actions(getDriver()).moveToElement(uiRoot, 0, 0).click().perform();
        // uiRoot.click();
        checkTooltipNotPresent();

        selectAndType(closeTimeout, "3000");
        moveMouseToTopLeft(uiRoot);
        testBenchElement(shortTooltip).showTooltip();
        waitForElementToBePresent(tooltipBy);
        WebElement tooltip2 = getTooltip();
        Assert.assertEquals("This is a short tooltip", tooltip2.getText());

        uiRoot.click();
        // assert that tooltip is present
        getTooltip();
        selectAndType(closeTimeout, "0");
        testBenchElement(longTooltip).showTooltip();
        waitForElementToBePresent(tooltipBy);
        Assert.assertEquals(500, getTooltip().getSize().getWidth());

        uiRoot.click();
        selectAndType(maxWidth, "100");
        moveMouseToTopLeft(uiRoot);
        testBenchElement(longTooltip).showTooltip();
        Assert.assertEquals(100, getTooltip().getSize().getWidth());
    }

    private WebElement getTooltip() {
        return getDriver().findElement(tooltipBy);
    }

    private void checkTooltipNotPresent() {
        try {
            WebElement tooltip = getTooltip();
            if (!"".equals(tooltip.getText())
                    || tooltip.getLocation().getX() > -999) {
                Assert.fail("Found tooltip that shouldn't be visible: "
                        + tooltip.getText() + " at " + tooltip.getLocation());
            }
        } catch (NoSuchElementException e) {
            Assert.fail("Tooltip element was removed completely, causing extra events to accessibility tools");
        }
    }

    private void selectAndType(WebElement element, String value) {
        // select and replace text
        element.clear();
        // if null representation not set as "", need to move cursor to end and
        // remove text "null"
        // element.sendKeys("" + Keys.BACK_SPACE + Keys.BACK_SPACE
        // + Keys.BACK_SPACE + Keys.BACK_SPACE);
        element.sendKeys(value + Keys.ENTER);
    }

    private void moveMouseToTopLeft(WebElement element) {
        new Actions(getDriver()).moveToElement(element, 0, 0).perform();
    }

}