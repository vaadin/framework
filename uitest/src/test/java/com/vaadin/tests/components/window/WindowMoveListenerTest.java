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
package com.vaadin.tests.components.window;

import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class WindowMoveListenerTest extends MultiBrowserTest {

    @Test
    public void testWindowRepositioning() throws Exception {
        openTestURL();

        final WebElement window = getDriver().findElement(By.id("testwindow"));
        WebElement button = getDriver().findElement(By.id("testbutton"));

        // I'd loved to use the header, but that doesn't work. Footer works
        // fine, though :)
        WebElement windowFooter = getDriver()
                .findElement(By.className("v-window-footer"));

        final Point winPos = window.getLocation();

        // move window
        Action a = new Actions(driver).clickAndHold(windowFooter)
                .moveByOffset(100, 100).release().build();
        a.perform();
        assertNotEquals("Window was not dragged correctly.", winPos.x,
                window.getLocation().x);
        assertNotEquals("Window was not dragged correctly.", winPos.y,
                window.getLocation().y);

        // re-set window
        button.click();

        waitUntilWindowHasReseted(window, winPos);
    }

    private void waitUntilWindowHasReseted(final WebElement window,
            final Point winPos) {
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return winPos.x == window.getLocation().x
                        && winPos.y == window.getLocation().y;
            }
        }, 5);
    }
}
