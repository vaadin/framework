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
package com.vaadin.tests.elements.tabsheet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TabSheetElementTabWithoutCaptionTest extends MultiBrowserTest {

    @Before
    public void init() {
        openTestURL("theme=reindeer");
    }

    @Test
    public void openTabByCaption() {
        for (int i = 1; i <= 5; i++) {
            String caption = (i != 3 ? "Tab " + i : null);
            $(TabSheetElement.class).first().openTab(caption);
            checkSelectedTab(caption);
        }
    }

    @Test
    public void getCaptions() {
        List<String> expectedCaptions = Arrays.asList(
                new String[] { "Tab 1", "Tab 2", null, "Tab 4", "Tab 5" });
        List<String> actualCaptions = $(TabSheetElement.class).first()
                .getTabCaptions();
        assertEquals("Unexpected tab captions", expectedCaptions,
                actualCaptions);
    }

    @Test
    public void closeTabByCaption() {
        for (int i = 1; i <= 5; i++) {
            String caption = (i != 3 ? "Tab " + i : null);
            $(TabSheetElement.class).first().closeTab(caption);
            checkTabClosed(caption);
        }
    }

    @Test
    public void openTabByIndex() {
        int maxIndex = $(TabSheetElement.class).get(1).getTabCount();
        for (int i = 0; i < maxIndex; i++) {
            $(TabSheetElement.class).all().get(1).openTab(i);
            checkIconTabOpen(i);
        }
    }

    @Test
    public void closeTabByIndex() {
        $(TabSheetElement.class).get(1).closeTab(0);
        int numTabs = $(TabSheetElement.class).get(1)
                .findElements(By.className("v-tabsheet-tabitemcell")).size();
        assertEquals("The number of open tabs is incorrect", 4, numTabs);
        $(TabSheetElement.class).get(1).closeTab(3);
        numTabs = $(TabSheetElement.class).get(1)
                .findElements(By.className("v-tabsheet-tabitemcell")).size();
        assertEquals("The number of open tabs is incorrect", 3, numTabs);
        $(TabSheetElement.class).get(1).closeTab(2);
        numTabs = $(TabSheetElement.class).get(1)
                .findElements(By.className("v-tabsheet-tabitemcell")).size();
        assertEquals("The number of open tabs is incorrect", 2, numTabs);
    }

    private void checkSelectedTab(String caption) {
        // Check that the currently selected tab has the given caption.
        WebElement elem = $(TabSheetElement.class).first().getWrappedElement();
        List<WebElement> openTabs = elem
                .findElements(By.className("v-tabsheet-tabitem-selected"));
        assertTrue(
                "Exactly one tab should be open, but there are "
                        + openTabs.size() + " open tabs.",
                openTabs.size() == 1);
        WebElement tab = openTabs.get(0);
        List<WebElement> openTabCaptionElements = tab
                .findElement(By.className("v-caption"))
                .findElements(By.className("v-captiontext"));
        if (openTabCaptionElements.size() > 0) {
            String openTabCaption = openTabCaptionElements.get(0).getText();
            assertEquals("Wrong tab is open.", caption, openTabCaption);
        } else {
            assertEquals("Wrong tab is open.", caption, null);
        }
    }

    private void checkTabClosed(String caption) {
        List<String> openTabs = $(TabSheetElement.class).first()
                .getTabCaptions();
        assertFalse(
                "The tab with caption " + caption
                        + " is present, although it should have been closed.",
                openTabs.contains(caption));
    }

    private void checkIconTabOpen(int index) {
        List<WebElement> tabs = $(TabSheetElement.class).get(1)
                .findElements(By.className("v-tabsheet-tabitemcell"));
        boolean tabsOpen = false;
        for (int i = 0; i < tabs.size(); i++) {
            WebElement tab = tabs.get(i);
            boolean isOpened = tab
                    .findElements(By.className("v-tabsheet-tabitem-selected"))
                    .size() > 0;
            if (isOpened) {
                tabsOpen = true;
                assertEquals("The wrong tab is open.", index, i);
            }
        }
        if (!tabsOpen) {
            fail("There are no tabs open, but tab with index " + index
                    + " should be open.");
        }
    }
}