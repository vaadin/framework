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
package com.vaadin.tests.components.grid.basicfeatures.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.ElementQuery;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;

public class GridDetailsTest extends GridBasicClientFeaturesTest {

    private static final String[] SET_GENERATOR = new String[] { "Component",
            "Row details", "Set generator" };
    private static final String[] SET_FAULTY_GENERATOR = new String[] {
            "Component", "Row details", "Set faulty generator" };
    private static final String[] SET_EMPTY_GENERATOR = new String[] {
            "Component", "Row details", "Set empty generator" };
    private static final String[] TOGGLE_DETAILS_FOR_ROW_1 = new String[] {
            "Component", "Row details", "Toggle details for row 1" };
    private static final String[] TOGGLE_DETAILS_FOR_ROW_100 = new String[] {
            "Component", "Row details", "Toggle details for row 100" };

    @Before
    public void setUp() {
        setDebug(true);
        openTestURL();
    }

    @Test(expected = NoSuchElementException.class)
    public void noDetailsByDefault() {
        assertNull("details for row 1 should not exist at the start",
                getGridElement().getDetails(1));
    }

    @Test
    public void nullRendererShowsDetailsPlaceholder() {
        selectMenuPath(TOGGLE_DETAILS_FOR_ROW_1);
        TestBenchElement details = getGridElement().getDetails(1);
        assertNotNull("details for row 1 should not exist at the start",
                details);
        assertTrue("details should've been empty for null renderer", details
                .getText().isEmpty());
    }

    @Test
    public void applyRendererThenOpenDetails() {
        selectMenuPath(SET_GENERATOR);
        selectMenuPath(TOGGLE_DETAILS_FOR_ROW_1);

        TestBenchElement details = getGridElement().getDetails(1);
        assertTrue("Unexpected details content",
                details.getText().startsWith("Row: 1."));
    }

    @Test
    public void openDetailsThenAppyRenderer() {
        selectMenuPath(TOGGLE_DETAILS_FOR_ROW_1);
        selectMenuPath(SET_GENERATOR);

        TestBenchElement details = getGridElement().getDetails(1);
        assertTrue("Unexpected details content",
                details.getText().startsWith("Row: 1."));
    }

    @Test
    public void openHiddenDetailsThenScrollToIt() {
        try {
            getGridElement().getDetails(100);
            fail("details row for 100 was apparently found, while it shouldn't have been.");
        } catch (NoSuchElementException e) {
            // expected
        }

        selectMenuPath(SET_GENERATOR);
        selectMenuPath(TOGGLE_DETAILS_FOR_ROW_100);

        // scroll a bit beyond so we see below.
        getGridElement().scrollToRow(101);

        TestBenchElement details = getGridElement().getDetails(100);
        assertTrue("Unexpected details content",
                details.getText().startsWith("Row: 100."));
    }

    @Test
    public void errorUpdaterShowsErrorNotification() {
        assertFalse("No notifications should've been at the start",
                $(NotificationElement.class).exists());

        selectMenuPath(TOGGLE_DETAILS_FOR_ROW_1);
        selectMenuPath(SET_FAULTY_GENERATOR);

        ElementQuery<NotificationElement> notification = $(NotificationElement.class);
        assertTrue("Was expecting an error notification here",
                notification.exists());
        notification.first().closeNotification();

        assertEquals("The error details element should be empty", "",
                getGridElement().getDetails(1).getText());
    }

    @Test
    public void updaterStillWorksAfterError() {
        selectMenuPath(TOGGLE_DETAILS_FOR_ROW_1);

        selectMenuPath(SET_FAULTY_GENERATOR);
        $(NotificationElement.class).first().closeNotification();
        selectMenuPath(SET_GENERATOR);

        assertNotEquals(
                "New details should've been generated even after error", "",
                getGridElement().getDetails(1).getText());
    }

    @Test
    public void updaterRendersExpectedWidgets() {
        selectMenuPath(SET_GENERATOR);
        selectMenuPath(TOGGLE_DETAILS_FOR_ROW_1);

        TestBenchElement detailsElement = getGridElement().getDetails(1);
        assertNotNull(detailsElement.findElement(By.className("gwt-Label")));
        assertNotNull(detailsElement.findElement(By.className("gwt-Button")));
    }

    @Test
    public void widgetsInUpdaterWorkAsExpected() {
        selectMenuPath(SET_GENERATOR);
        selectMenuPath(TOGGLE_DETAILS_FOR_ROW_1);

        TestBenchElement detailsElement = getGridElement().getDetails(1);
        WebElement button = detailsElement.findElement(By
                .className("gwt-Button"));
        button.click();

        WebElement label = detailsElement
                .findElement(By.className("gwt-Label"));
        assertEquals("clicked", label.getText());
    }

    @Test
    public void emptyGenerator() {
        selectMenuPath(SET_EMPTY_GENERATOR);
        selectMenuPath(TOGGLE_DETAILS_FOR_ROW_1);

        assertEquals("empty generator did not produce an empty details row",
                "", getGridElement().getDetails(1).getText());
    }

    @Test(expected = NoSuchElementException.class)
    public void removeDetailsRow() {
        selectMenuPath(SET_GENERATOR);
        selectMenuPath(TOGGLE_DETAILS_FOR_ROW_1);
        selectMenuPath(TOGGLE_DETAILS_FOR_ROW_1);

        getGridElement().getDetails(1);
    }
}
