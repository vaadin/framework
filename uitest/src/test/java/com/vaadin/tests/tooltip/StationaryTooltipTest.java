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
package com.vaadin.tests.tooltip;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class StationaryTooltipTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingTooltip();
    }

    @Test
    public void tooltipShouldBeStationary() throws InterruptedException {
        openTestURL();

        Mouse mouse = getMouse();

        moveMouseToButtonUpperLeftCorner(mouse);
        sleep(3000); // wait for the tooltip to become visible
        int originalTooltipLocationX = getTooltipLocationX();

        moveMouseToButtonBottomRightCorner(mouse);
        int actualTooltipLocationX = getTooltipLocationX();

        assertThat(actualTooltipLocationX, is(greaterThan(0)));
        assertThat(actualTooltipLocationX, is(originalTooltipLocationX));
    }

    private Coordinates getButtonCoordinates() {
        return getCoordinates(getButtonElement());
    }

    private ButtonElement getButtonElement() {
        return $(ButtonElement.class).first();
    }

    private void moveMouseToButtonBottomRightCorner(Mouse mouse) {
        Coordinates buttonCoordinates = getButtonCoordinates();
        Dimension buttonDimensions = getButtonDimensions();

        mouse.mouseMove(buttonCoordinates, buttonDimensions.getWidth() - 1,
                buttonDimensions.getHeight() - 1);
    }

    private void moveMouseToButtonUpperLeftCorner(Mouse mouse) {
        Coordinates buttonCoordinates = getButtonCoordinates();

        mouse.mouseMove(buttonCoordinates, 0, 0);
    }

    private org.openqa.selenium.Dimension getButtonDimensions() {
        ButtonElement buttonElement = getButtonElement();

        return buttonElement.getWrappedElement().getSize();
    }

    private int getTooltipLocationX() {
        return getTooltipElement().getLocation().getX();
    }

}
