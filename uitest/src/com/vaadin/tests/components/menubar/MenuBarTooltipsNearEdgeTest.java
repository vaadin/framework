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
package com.vaadin.tests.components.menubar;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to see if tooltips will render in the correct locations near the edges.
 * 
 * @author Vaadin Ltd
 */
public class MenuBarTooltipsNearEdgeTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Tooltip tests work unreliably on IE due to an issue with the
        // WebDriver (see #13854)
        List<DesiredCapabilities> browsers = super.getBrowsersToTest();
        browsers.remove(Browser.IE8.getDesiredCapabilities());
        return browsers;
    };

    @Test
    public void testTooltipLocation() {
        openTestURL();
        Mouse mouse = ((HasInputDevices) getDriver()).getMouse();
        WebElement menu = $(MenuBarElement.class).first().getWrappedElement();
        Coordinates menuLocation = ((Locatable) menu).getCoordinates();
        mouse.click(menuLocation);
        mouse.mouseMove(menuLocation, 5, -40);
        WebElement tooltip = getTooltipElement();
        assertThat(tooltip.getLocation().x, is(lessThan(menuLocation.onPage().x
                - tooltip.getSize().getWidth())));

    }
}
