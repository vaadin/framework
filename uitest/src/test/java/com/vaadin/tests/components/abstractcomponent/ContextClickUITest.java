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
package com.vaadin.tests.components.abstractcomponent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.UIElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ContextClickUITest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingContextMenu();
    }

    @Test
    public void testContextClick() {
        openTestURL();

        new Actions(getDriver())
                .moveToElement($(UIElement.class).first(), 10, 10)
                .contextClick().perform();

        assertEquals("Context click not received correctly",
                "1. Received context click at (10, 10)", getLogRow(0));
    }

    @Test
    public void testRemoveListener() {
        openTestURL();

        $(ButtonElement.class).first().click();

        new Actions(getDriver())
                .moveToElement($(UIElement.class).first(), 50, 50)
                .contextClick().perform();

        new Actions(getDriver())
                .moveToElement($(UIElement.class).first(), 10, 10).click()
                .perform();

        assertTrue("Context click should not be handled.",
                getLogRow(0).trim().isEmpty());
    }
}
