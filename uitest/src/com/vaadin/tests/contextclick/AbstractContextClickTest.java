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
package com.vaadin.tests.contextclick;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.AbstractComponentElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("contextclick")
public abstract class AbstractContextClickTest extends MultiBrowserTest {

    private Pattern defaultLog = Pattern
            .compile("[0-9]+. ContextClickEvent: [(]([0-9]+), ([0-9]+)[)]");

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingContextMenu();
    }

    @Test
    public void testDefaultListener() {
        openTestURL();

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

        contextClick(component);

        Point l = component.getLocation();

        Matcher matcher = defaultLog.matcher(getLogRow(0));
        Assert.assertTrue(
                "Log row content did not match default listener output: "
                        + getLogRow(0), matcher.find());

        int xCoord = Integer.parseInt(matcher.group(1));
        int yCoord = Integer.parseInt(matcher.group(2));

        int xExpected = l.getX() + 10;
        int yExpected = l.getY() + 10;

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
     *            x coordinate
     * @param yCoord
     *            y coordinate
     */
    protected void contextClick(WebElement e, int xCoord, int yCoord) {
        new Actions(getDriver()).moveToElement(e, xCoord, yCoord)
                .contextClick().perform();
        new Actions(getDriver()).moveToElement(e, xCoord - 5, yCoord - 5)
                .click().perform();
    }

}
