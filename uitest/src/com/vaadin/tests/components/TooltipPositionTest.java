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
package com.vaadin.tests.components;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Window;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that the tooltip is positioned so that it fits in the displayed area.
 * 
 * @author Vaadin Ltd
 */
public class TooltipPositionTest extends MultiBrowserTest {

    @Test
    public void testRegression_EmptyTooltipShouldNotBeAppearedDuringInitialization()
            throws Exception {
        openTestURL();

        waitForElementVisible(By.cssSelector(".v-tooltip"));
        WebElement tooltip = driver.findElement(By.cssSelector(".v-tooltip"));

        Assert.assertTrue(
                "This init tooltip with text ' ' is present in the DOM and should be entirely outside the browser window",
                isOutsideOfWindow(tooltip));
    }

    @Test
    public void testTooltipPosition() throws Exception {
        openTestURL();
        for (int i = 0; i < TooltipPosition.NUMBER_OF_BUTTONS; i++) {
            ButtonElement button = $(ButtonElement.class).get(i);
            // Move the mouse to display the tooltip.
            Actions actions = new Actions(driver);
            actions.moveToElement(button, 10, 10);
            actions.build().perform();
            waitUntil(tooltipToBeInsideWindow(By.cssSelector(".v-tooltip"),
                    driver.manage().window()));

            if (i < TooltipPosition.NUMBER_OF_BUTTONS - 1) {
                // Remove the tooltip by moving the mouse.
                actions = new Actions(driver);
                actions.moveByOffset(300, 0);
                actions.build().perform();
                waitUntil(tooltipNotToBeShown(By.cssSelector(".v-tooltip"),
                        driver.manage().window()));
            }
        }
    }

    /*
     * An expectation for checking that the tooltip found by the given locator
     * is present in the DOM and entirely inside the browser window. The
     * coordinate of the top left corner of the window is supposed to be (0, 0).
     */
    private ExpectedCondition<Boolean> tooltipToBeInsideWindow(
            final By tooltipLocator, final Window window) {
        return new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                List<WebElement> elements = findElements(tooltipLocator);
                if (elements.isEmpty()) {
                    return false;
                }
                WebElement element = elements.get(0);
                try {
                    if (!element.isDisplayed()) {
                        return false;
                    }
                    Point topLeft = element.getLocation();
                    int xLeft = topLeft.getX();
                    int yTop = topLeft.getY();
                    if (xLeft < 0 || yTop < 0) {
                        return false;
                    }
                    Dimension elementSize = element.getSize();
                    int xRight = xLeft + elementSize.getWidth() - 1;
                    int yBottom = yTop + elementSize.getHeight() - 1;
                    Dimension browserSize = window.getSize();
                    return xRight < browserSize.getWidth()
                            && yBottom < browserSize.getHeight();
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            }

            @Override
            public String toString() {
                return "the tooltip to be displayed inside the window";
            }
        };
    };

    /*
     * An expectation for checking that the tooltip found by the given locator
     * is not shown in the window, even partially. The top left corner of window
     * should have coordinates (0, 0).
     */
    private ExpectedCondition<Boolean> tooltipNotToBeShown(
            final By tooltipLocator, final Window window) {
        return new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                List<WebElement> elements = findElements(tooltipLocator);
                if (elements.isEmpty()) {
                    return true;
                }
                WebElement tooltip = elements.get(0);
                try {
                    return isOutsideOfWindow(tooltip);
                } catch (StaleElementReferenceException e) {
                    return true;
                }
            }

            @Override
            public String toString() {
                return "the tooltip not to be displayed inside the window";
            }

        };
    }

    private boolean isOutsideOfWindow(WebElement tooltip) {
        if (!tooltip.isDisplayed()) {
            return true;
        }
        // The tooltip is shown, at least partially, if
        // its intervals of both horizontal and vertical coordinates
        // overlap those of the window.
        Point topLeft = tooltip.getLocation();
        Dimension tooltipSize = tooltip.getSize();
        Dimension windowSize = driver.manage().window().getSize();
        int xLeft = topLeft.getX();
        int yTop = topLeft.getY();
        int xRight = xLeft + tooltipSize.getWidth() - 1;
        int yBottom = yTop + tooltipSize.getHeight() - 1;
        boolean overlapHorizontally = !(xRight < 0 || xLeft >= windowSize
                .getWidth());
        boolean overlapVertically = !(yBottom < 0 || yTop >= windowSize
                .getHeight());
        return !(overlapHorizontally && overlapVertically);
    }
}