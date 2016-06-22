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
package com.vaadin.tests.components.tabsheet;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests removing tabs that have been scrolled out of view. This should cause no
 * change to the scroll position.
 * 
 * @author Vaadin Ltd
 */
public class TabSheetScrollOnTabCloseTest extends MultiBrowserTest {

    @Test
    public void testScrollPositionAfterClosing() throws Exception {
        openTestURL();
        TabSheetElement ts = $(TabSheetElement.class).first();
        WebElement tabSheetScroller = ts.findElement(By
                .className("v-tabsheet-scrollerNext"));
        // scroll to the right
        for (int i = 0; i < 4; i++) {
            tabSheetScroller.click();
        }
        // check that tab 4 is the first visible tab
        checkDisplayedStatus(ts, "tab3", false);
        checkDisplayedStatus(ts, "tab4", true);
        // remove tabs from the left, check that tab4 is still the first visible
        // tab
        for (int i = 0; i < 4; i++) {
            $(ButtonElement.class).get(i).click();
            checkDisplayedStatus(ts, "tab3" + i, false);
            checkDisplayedStatus(ts, "tab4", true);
            checkDisplayedStatus(ts, "tab6", true);
        }
        // remove tabs from the right and check scroll position
        for (int i = 7; i < 10; i++) {
            $(ButtonElement.class).get(i).click();
            checkFirstTab(ts, "tab4");
            checkDisplayedStatus(ts, "tab6", true);
        }
    }

    /**
     * Checks that the visible status of the tab with the given id is equal to
     * shouldBeVisible. That is, the tab with the given id should be visible if
     * and only if shouldBeVisible is true. Used for checking that the leftmost
     * visible tab is the expected one when there should be tabs (hidden because
     * of scroll position) to the left of tabId.
     * 
     * If there is no tab with the specified id, the tab is considered not to be
     * visible.
     */
    private void checkDisplayedStatus(TabSheetElement tabSheet, String tabId,
            boolean shouldBeVisible) {
        org.openqa.selenium.By locator = By.cssSelector("#" + tabId);
        waitUntil(visibilityOfElement(locator, shouldBeVisible));
    }

    /**
     * Checks that there are no hidden tabs in tabSheet and that the id of the
     * first tab is tabId. Used for checking that the leftmost visible tab is
     * the expected one when there are no tabs to the left of the tab with the
     * given id. When there are tabs to the left of tabId, check instead that
     * tabId is visible and the previous tab is hidden (see
     * checkDisplayedStatus).
     */
    private void checkFirstTab(TabSheetElement tabSheet, String tabId) {
        waitUntil(visibilityOfElement(
                By.cssSelector(".v-tabsheet-tabitemcell[aria-hidden]"), false));
        waitUntil(leftmostTabHasId(tabSheet, tabId));
    }

    /**
     * An expectation for checking that the visibility status of the specified
     * element is correct. If the element does not exist in the DOM, it is
     * considered not to be visible. If several elements match the locator, only
     * the visibility of the first matching element is considered.
     * 
     * @param locator
     *            used to find the element
     * @param expectedVisibility
     *            whether the element should be visible
     */
    private static ExpectedCondition<Boolean> visibilityOfElement(
            final org.openqa.selenium.By locator,
            final boolean expectedVisibility) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                List<WebElement> matchingElements = driver
                        .findElements(locator);
                if (matchingElements.isEmpty()) {
                    return !expectedVisibility;
                } else {
                    try {
                        WebElement first = matchingElements.get(0);
                        return first.isDisplayed() == expectedVisibility;
                    } catch (StaleElementReferenceException e) {
                        // The element was initially in DOM but has been
                        // removed.
                        return !expectedVisibility;
                    }
                }
            }

            @Override
            public String toString() {
                return "element " + (expectedVisibility ? "" : "not ")
                        + "expected to be visible: " + locator;
            }
        };
    }

    /**
     * An expectation for checking that the leftmost tab has id equal to tabId.
     * 
     * @param tabSheet
     *            the tab sheet containing the tab
     * @param tabId
     *            the id of the tab that should be the leftmost tab
     */
    private static ExpectedCondition<Boolean> leftmostTabHasId(
            final TabSheetElement tabSheet, final String tabId) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    WebElement leftElement = tabSheet.findElement(By
                            .cssSelector(".v-tabsheet-tabitemcell"));
                    String leftId = leftElement.getAttribute("id");
                    return leftId.equals(tabId);
                } catch (NoSuchElementException e) {
                    return false;
                }
            }

            @Override
            public String toString() {
                return "expected tab index of the leftmost tab in the tab sheet: "
                        + tabId;
            }
        };
    }
}