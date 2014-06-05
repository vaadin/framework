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
package com.vaadin.tests.components.window;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * 
 * @since
 * @author Vaadin Ltd
 */
public class TooltipInWindowTest extends MultiBrowserTest {

    @Test
    public void testTooltipsInSubWindow() throws InterruptedException {
        openTestURL();

        WebElement textfield = vaadinElementById("tf1");
        Coordinates textfieldCoordinates = ((Locatable) textfield)
                .getCoordinates();

        Mouse mouse = ((HasInputDevices) getDriver()).getMouse();

        // Show tooltip
        mouse.mouseMove(textfieldCoordinates, 10, 10);

        sleep(100);
        ensureVisibleTooltipPositionedCorrectly();
        assertEquals("My tooltip", getTooltipElement().getText());

        // Hide tooltip
        mouse.mouseMove(textfieldCoordinates, -100, -100);
        sleep(2000);

        ensureHiddenTooltipPositionedCorrectly();
        assertEquals("", getTooltipElement().getText());

        // Show tooltip again
        mouse.mouseMove(textfieldCoordinates, 10, 10);

        sleep(100);
        ensureVisibleTooltipPositionedCorrectly();
        assertEquals("My tooltip", getTooltipElement().getText());

        // Hide tooltip
        mouse.mouseMove(textfieldCoordinates, -100, -100);
        sleep(2000);

        ensureHiddenTooltipPositionedCorrectly();
        assertEquals("", getTooltipElement().getText());

    }

    private WebElement getTooltipElement() {
        return getDriver().findElement(By.className("v-tooltip-text"));
    }

    private WebElement getTooltipContainerElement() {
        return getDriver().findElement(By.className("v-tooltip"));
    }

    private void ensureVisibleTooltipPositionedCorrectly() {
        WebElement textfield = vaadinElementById("tf1");
        int tooltipX = getTooltipContainerElement().getLocation().getX();
        int textfieldX = textfield.getLocation().getX();
        assertGreaterOrEqual("Tooltip should be positioned on the textfield ("
                + tooltipX + " < " + textfieldX + ")", tooltipX, textfieldX);
    }

    private void ensureHiddenTooltipPositionedCorrectly() {
        int tooltipX = getTooltipContainerElement().getLocation().getX();
        assertLessThanOrEqual(
                "Tooltip should be positioned outside of viewport (was at "
                        + tooltipX + ")", tooltipX, -1000);
    }
}
