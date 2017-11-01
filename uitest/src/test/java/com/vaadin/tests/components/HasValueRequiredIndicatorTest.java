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
package com.vaadin.tests.components;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 *
 */
public abstract class HasValueRequiredIndicatorTest extends MultiBrowserTest {

    @Test
    public void requiredIndicatorVisible() {
        openTestURL();
        List<WebElement> layouts = findElements(By.className("vaadin-layout"));
        assertFalse(layouts.isEmpty());
        layouts.stream().forEach(this::checkRequiredIndicator);
    }

    protected void checkRequiredIndicator(WebElement layout) {
        WebElement caption = layout.findElement(By.className("v-caption"));
        assertTrue(caption.isDisplayed());
        WebElement indicator = caption
                .findElement(By.className("v-required-field-indicator"));
        assertTrue(indicator.isDisplayed());
        Point layoutLocation = layout.getLocation();
        Point indicatorLocation = indicator.getLocation();
        assertTrue("Indicator x-axis location is not inside layout",
                indicatorLocation.getX() >= layoutLocation.getX());
        assertTrue("Indicator y-axis location is not inside layout",
                indicatorLocation.getY() >= layoutLocation.getY());
    }
}
