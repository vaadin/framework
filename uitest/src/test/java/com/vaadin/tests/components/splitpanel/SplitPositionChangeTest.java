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
package com.vaadin.tests.components.splitpanel;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.HorizontalSplitPanelElement;
import com.vaadin.testbench.elements.VerticalSplitPanelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for {@link SplitPositionChangeListeners}.
 * 
 * @author Vaadin Ltd
 */
public class SplitPositionChangeTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        waitForElementPresent(By.className("v-splitpanel-horizontal"));
    }

    @Test
    public void testHorizontalSplit() {
        HorizontalSplitPanelElement split = $(HorizontalSplitPanelElement.class)
                .first();
        WebElement splitter = split.findElement(By
                .className("v-splitpanel-hsplitter"));
        int position = splitter.getLocation().getX();
        Actions actions = new Actions(driver);
        actions.clickAndHold(splitter).moveByOffset(50, 0).release().perform();
        assertPosition(position, splitter.getLocation().getX());
        assertLogText(true);
    }

    @Test
    public void testVerticalSplit() {
        VerticalSplitPanelElement split = $(VerticalSplitPanelElement.class)
                .first();
        WebElement splitter = split.findElement(By
                .className("v-splitpanel-vsplitter"));
        int position = splitter.getLocation().getY();
        Actions actions = new Actions(driver);
        actions.clickAndHold(splitter).moveByOffset(0, 50).release().perform();
        assertPosition(position, splitter.getLocation().getY());
        assertLogText(false);
    }

    private void assertPosition(int original, int current) {
        Assert.assertFalse("Position didn't change", original == current);
    }

    private void assertLogText(boolean horizontal) {
        String expected = String.format(
                "1. Split position changed: %s, position: .*",
                horizontal ? "horizontal" : "vertical");
        String actual = getLogRow(0);
        Assert.assertTrue(
                String.format(
                        "Log content didn't match the expected format.\nexpected: '%s'\nwas: '%s'",
                        expected, actual), actual.matches(expected));
    }
}
