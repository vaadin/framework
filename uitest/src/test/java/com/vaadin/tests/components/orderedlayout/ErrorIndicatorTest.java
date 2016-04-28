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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ErrorIndicatorTest extends MultiBrowserTest {

    @Test
    public void verifyTooltips() {
        String tooltipText;
        openTestURL();

        showTooltip($(TextFieldElement.class).first());
        tooltipText = driver.findElement(By.className("v-tooltip")).getText();
        assertEquals(tooltipText, "Vertical layout tooltip");

        showTooltip($(TextFieldElement.class).get(1));
        tooltipText = driver.findElement(By.className("v-tooltip")).getText();
        assertEquals(tooltipText, "Horizontal layout tooltip");
    }

    private void showTooltip(WebElement element) {
        Coordinates elementCoordinates = ((Locatable) element).getCoordinates();
        Mouse mouse = ((HasInputDevices) getDriver()).getMouse();
        mouse.mouseMove(elementCoordinates);
    }
}
