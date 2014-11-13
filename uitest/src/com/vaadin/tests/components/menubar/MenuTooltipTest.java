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

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to see if tooltips on menu items obscure other items on the menu.
 * 
 * @author Vaadin Ltd
 */
public class MenuTooltipTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersExcludingIE();
    }

    @Test
    public void testToolTipDelay() throws InterruptedException {
        openTestURL();

        Coordinates elementCoordinates = getCoordinates($(MenuBarElement.class)
                .first());

        Mouse mouse = ((HasInputDevices) getDriver()).getMouse();

        mouse.click(elementCoordinates);
        mouse.mouseMove(elementCoordinates, 15, 40);

        sleep(1000);

        assertThat(getTooltipElement().getLocation().getX(),
                is(lessThan(-1000)));

        sleep(3000);

        assertThat(getTooltipElement().getLocation().getX(),
                is(greaterThan(elementCoordinates.onPage().getX())));
        assertThat(getTooltipElement().getText(), is("TOOLTIP 1"));
    }
}
