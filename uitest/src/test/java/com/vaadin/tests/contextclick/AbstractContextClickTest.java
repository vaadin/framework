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
package com.vaadin.tests.contextclick;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.AbstractComponentElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("contextclick")
public abstract class AbstractContextClickTest extends MultiBrowserTest {

    private Pattern defaultLog = Pattern
            .compile("[0-9]+. ContextClickEvent: [(]([0-9]+), ([0-9]+)[)]");

    @Override
    protected boolean useNativeEventsForIE() {
        return false;
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingContextMenu();
    }

    @Before
    public void setUp() {
        openTestURL();
    }

    @Test
    public void testDefaultListener() {
        addOrRemoveDefaultListener();

        assertDefaultContextClickListener(1);
    }

    protected void assertNoContextClickHandler() {
        AbstractComponentElement component = $(AbstractComponentElement.class)
                .id("testComponent");

        String log = getLogRow(0);

        contextClick(component);

        assertEquals("Log entry without a context click listener.", log,
                getLogRow(0));

    }

    protected void assertDefaultContextClickListener(int index) {
        AbstractComponentElement component = $(AbstractComponentElement.class)
                .id("testComponent");

        int x = 20;
        int y = 20;
        contextClick(component, x, y);

        Point l = component.getLocation();

        Matcher matcher = defaultLog.matcher(getLogRow(0));
        Assert.assertTrue(
                "Log row content did not match default listener output: "
                        + getLogRow(0),
                matcher.find());

        int xCoord = Integer.parseInt(matcher.group(1));
        int yCoord = Integer.parseInt(matcher.group(2));

        int xExpected = l.getX() + x;
        int yExpected = l.getY() + y;

        Assert.assertTrue(
                "X Coordinate differs too much from expected. Expected: "
                        + xExpected + ", actual: " + xCoord,
                Math.abs(xExpected - xCoord) <= 1);
        Assert.assertTrue(
                "Y Coordinate differs too much from expected. Expected: "
                        + yExpected + ", actual: " + yCoord,
                Math.abs(yExpected - yCoord) <= 1);
    }

    protected void addOrRemoveDefaultListener() {
        $(ButtonElement.class).caption("Add/Remove default listener").first()
                .click();
    }

    protected void addOrRemoveTypedListener() {
        $(ButtonElement.class).caption("Add/Remove typed listener").first()
                .click();
    }

    /**
     * Performs a context click on given element at coordinates 10, 10 followed
     * by a regular click. This prevents browser context menu from blocking
     * future operations.
     *
     * @param e
     *            web element
     */
    protected void contextClick(WebElement e) {
        contextClick(e, 10, 10);
    }

    /**
     * Performs a context click on given element at given coordinates followed
     * by a regular click. This prevents browser context menu from blocking
     * future operations.
     *
     * @param e
     *            web element
     * @param xCoord
     *            x coordinate relative to the top-left corner of the element
     * @param yCoord
     *            y coordinate relative to the top-left corner of the element
     */
    protected void contextClick(WebElement e, int xCoord, int yCoord) {
        if (BrowserUtil.isFirefox(getDesiredCapabilities())) {
            // Workaround for Selenium/TB and Firefox 45 issue
            int x = e.getLocation().getX() + xCoord;
            int y = e.getLocation().getY() + yCoord;
            getCommandExecutor().executeScript(
                    "var ev = document.createEvent('MouseEvents'); ev.initMouseEvent('contextmenu', true, true, document.defaultView, 1, arguments[1], arguments[2], arguments[1], arguments[2], false, false, false, false, 2, null); arguments[0].dispatchEvent(ev);",
                    e, x, y);
            // make sure browser context menu does not block the test
            getCommandExecutor().executeScript(
                    "var ev = document.createEvent('MouseEvents'); ev.initMouseEvent('click', true, true, document.defaultView, 1, arguments[1]-5, arguments[2]-5, arguments[1]-5, arguments[2]-5, false, false, false, false, 1, null); arguments[0].dispatchEvent(ev);",
                    e, x, y);
        } else {
            new Actions(getDriver()).moveToElement(e, xCoord, yCoord)
                    .contextClick().moveByOffset(-5, -5).click().perform();
        }
    }

}
