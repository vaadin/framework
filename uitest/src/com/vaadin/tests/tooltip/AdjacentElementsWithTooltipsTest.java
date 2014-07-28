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

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to see if tooltips obey quickOpenDelay when moving between directly
 * adjacent elements.
 * 
 * @author Vaadin Ltd
 */
public class AdjacentElementsWithTooltipsTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersExcludingIE();
    }

    @Test
    public void tooltipsHaveQuickOpenDelay() throws InterruptedException {
        openTestURL();
        Coordinates button0Coordinates = getButtonCoordinates("Button 0");
        Coordinates button1Coordinates = getButtonCoordinates("Button 1");

        Mouse mouse = getMouse();
        mouse.mouseMove(button0Coordinates, 10, 10);
        sleep(1000);
        assertThat(getTooltipElement().getLocation().x, is(greaterThan(0)));

        mouse.mouseMove(button1Coordinates, 10, 10);
        assertThat(getTooltipElement().getLocation().x, is(lessThan(-1000)));

        sleep(1000);
        assertThat(getTooltipElement().getLocation().x,
                is(greaterThan(button1Coordinates.onPage().x)));
    }

    private Coordinates getButtonCoordinates(String caption) {
        return getCoordinates(getButton(caption));
    }

    private ButtonElement getButton(String caption) {
        return $(ButtonElement.class).caption(caption).first();
    }
}
