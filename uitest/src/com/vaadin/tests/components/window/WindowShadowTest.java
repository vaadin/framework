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

import java.awt.AWTException;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class WindowShadowTest extends MultiBrowserTest {

    @Test
    public void dragBackgroundWindow() throws AWTException, IOException,
            InterruptedException {
        openTestURL();
        WebElement wnd = getDriver().findElement(By.id("topwindow"));
        // There is some bug in Selenium. Can't move window using header
        // need use footer instead.
        WebElement wnd1Footer = wnd
                .findElement(By.className("v-window-footer"));
        Point startLoc = wnd.getLocation();
        Coordinates footerCoordinates = ((Locatable) wnd1Footer)
                .getCoordinates();
        Mouse mouse = ((HasInputDevices) getDriver()).getMouse();
        mouse.mouseDown(footerCoordinates);
        mouse.mouseMove(footerCoordinates, 200, 200);
        mouse.mouseUp(footerCoordinates);
        Point endLoc = wnd.getLocation();
        // don't compare to specific coordinate, because in IE9 and IE11
        // the window position is random.
        // So, checkt that the window was moved
        org.junit.Assert.assertNotEquals(startLoc, endLoc);
    }

    // IE8 doesn't support shadow-box css rule
    // ignore this browser in testing
    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersExcludingIE8();
    }
}