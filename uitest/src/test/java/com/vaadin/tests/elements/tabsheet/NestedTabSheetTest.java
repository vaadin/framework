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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests selecting tabs in a nested tab sheet. TabSheetElement.openTab should
 * not open tabs that are in a tab sheet that is itself contained in the current
 * tab sheet. Only the tabs in the current tab sheet should be candidates for
 * selection.
 */
public class NestedTabSheetTest extends MultiBrowserTest {

    @Test
    public void openOuterTabInNestedTabSheet() {
        openTestURL();
        // Open a tab in the outer tab sheet. No errors should occur.
        TabSheetElement outer = $(TabSheetElement.class).first();
        outer.openTab("Tab 2");
        checkForSelectedTab(outer, "Tab 2");
    }

    @Test
    public void openInnerTabInNestedTabSheet() {
        openTestURL();
        // Properly open a tab that is in an inner tab sheet.
        TabSheetElement outer = $(TabSheetElement.class).first();
        outer.openTab("Tab 3");
        TabSheetElement thirdInner = outer.$(TabSheetElement.class).first();
        thirdInner.openTab("Tab 3.2");
        checkForSelectedTab(thirdInner, "Tab 3.2");
    }

    @Test
    public void testThatOpeningInnerTabFails() {
        openTestURL();
        // Attempt to improperly open an inner tab. This should fail.
        TabSheetElement outer = $(TabSheetElement.class).first();
        try {
            outer.openTab("Tab 1.3");
        } catch (NoSuchElementException e) {
            // openTab may throw an exception when the tab is not found.
        }
        // Check that inner tab 1.3 is not selected.
        TabSheetElement inner = outer.$(TabSheetElement.class).first();
        assertFalse("Tab 1.3 is selected, but it should not be.",
                isTabSelected(inner, "Tab 1.3"));
    }

    private void checkForSelectedTab(TabSheetElement tse, String tabCaption) {
        assertTrue("Tab " + tabCaption + " should be selected, but it is not.",
                isTabSelected(tse, tabCaption));
    }

    private boolean isTabSelected(TabSheetElement tse, String tabCaption) {
        List<WebElement> selectedTabs = tse
                .findElements(By.className("v-tabsheet-tabitemcell-selected"));
        for (WebElement tab : selectedTabs) {
            WebElement caption = tab.findElement(By.className("v-captiontext"));
            if (tabCaption.equals(caption.getText())) {
                return true;
            }
        }
        return false;
    }
}