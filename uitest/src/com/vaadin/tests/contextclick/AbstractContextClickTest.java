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

import org.junit.Test;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.AbstractComponentElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

public abstract class AbstractContextClickTest extends MultiBrowserTest {

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

        int drift = 0;
        // IE 10 and 11 report different Y location.
        if (BrowserUtil.isIE(getDesiredCapabilities(), 10)
                || BrowserUtil.isIE(getDesiredCapabilities(), 11)) {
            drift = 1;
        }

        assertEquals(index + ". ContextClickEvent: (" + (l.getX() + 10) + ", "
                + (l.getY() + 10 + drift) + ")", getLogRow(0));
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
     * Performs a context click followed by a regular click. This prevents
     * browser context menu from blocking future operations.
     * 
     * @param e
     *            web element
     */
    protected void contextClick(WebElement e) {
        new Actions(getDriver()).moveToElement(e, 10, 10).contextClick()
                .perform();
        new Actions(getDriver()).moveToElement(e, 5, 5).click().perform();
    }
}
