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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests hovering over caption in nested layout
 */
public class NestedLayoutCaptionHoverTest extends MultiBrowserTest {

    @Test
    public void testTooltipInNestedLayout() throws Exception {
        openTestURL();

        WebElement caption = getDriver().findElement(
                By.className("v-captiontext"));

        assertEquals("inner layout", caption.getText());

        // Hover over the caption
        Coordinates coords = ((Locatable) caption).getCoordinates();
        ((HasInputDevices) getDriver()).getMouse().mouseMove(coords);
        sleep(1000);

        String selector = "Root/VNotification[0]";
        try {
            // Verify that there's no error notification
            vaadinElement(selector);
            fail("No error notification should be found");
        } catch (NoSuchElementException e) {
            // Exception caught. Verify it's the right one.
            assertTrue(e.getMessage().contains(selector));
        }
    }
}
