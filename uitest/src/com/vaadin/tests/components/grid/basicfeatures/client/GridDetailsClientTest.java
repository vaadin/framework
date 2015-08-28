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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.vaadin.shared.ui.grid.Range;
import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.testbench.By;
import com.vaadin.testbench.ElementQuery;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;
import com.vaadin.tests.tb3.newelements.FixedNotificationElement;

public class GridDetailsClientTest extends GridBasicClientFeaturesTest {

    private static final String[] SET_GENERATOR = new String[] { "Component",
            "Row details", "Set generator" };
    private static final String[] SET_FAULTY_GENERATOR = new String[] {
            "Component", "Row details", "Set faulty generator" };
    private static final String[] SET_EMPTY_GENERATOR = new String[] {
            "Component", "Row details", "Set empty generator" };

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
        toggleDetailsFor(1);
        TestBenchElement details = getGridElement().getDetails(1);
        assertNotNull("details for row 1 should not exist at the start",
                details);
        assertTrue("details should've been empty for null renderer", details
                .getText().isEmpty());
    }

    @Test
    public void applyRendererThenOpenDetails() {
        selectMenuPath(SET_GENERATOR);
        toggleDetailsFor(1);

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
        toggleDetailsFor(100);

        // scroll a bit beyond so we see below.
        getGridElement().scrollToRow(101);

        TestBenchElement details = getGridElement().getDetails(100);
        assertTrue("Unexpected details content",
                details.getText().startsWith("Row: 100."));
    }

    @Test
    public void errorUpdaterShowsErrorNotification() {
        assertFalse("No notifications should've been at the start",
                $(FixedNotificationElement.class).exists());

        selectMenuPath(SET_FAULTY_GENERATOR);
        toggleDetailsFor(1);

        ElementQuery<FixedNotificationElement> notification = $(FixedNotificationElement.class);
        assertTrue("Was expecting an error notification here",
                notification.exists());
        notification.first().close();

        assertEquals("The error details element should be empty", "",
                getGridElement().getDetails(1).getText());
    }

    @Test(expected = NoSuchElementException.class)
    public void detailsClosedWhenResettingGenerator() {

        selectMenuPath(SET_GENERATOR);
        toggleDetailsFor(1);

        selectMenuPath(SET_FAULTY_GENERATOR);

        getGridElement().getDetails(1);
    }

    @Test
    public void updaterRendersExpectedWidgets() {
        selectMenuPath(SET_GENERATOR);
        toggleDetailsFor(1);

        TestBenchElement detailsElement = getGridElement().getDetails(1);
        assertNotNull(detailsElement.findElement(By.className("gwt-Label")));
        assertNotNull(detailsElement.findElement(By.className("gwt-Button")));
    }

    @Test
    public void widgetsInUpdaterWorkAsExpected() {
        selectMenuPath(SET_GENERATOR);
        toggleDetailsFor(1);

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
        toggleDetailsFor(1);

        assertEquals("empty generator did not produce an empty details row",
                "", getGridElement().getDetails(1).getText());
    }

    @Test(expected = NoSuchElementException.class)
    public void removeDetailsRow() {
        selectMenuPath(SET_GENERATOR);
        toggleDetailsFor(1);
        toggleDetailsFor(1);

        getGridElement().getDetails(1);
    }

    @Test
    public void rowElementClassNames() {
        toggleDetailsFor(0);
        toggleDetailsFor(1);

        List<WebElement> elements = getGridElement().findElements(
                By.className("v-grid-spacer"));
        assertEquals("v-grid-spacer", elements.get(0).getAttribute("class"));
        assertEquals("v-grid-spacer stripe",
                elements.get(1).getAttribute("class"));
    }

    @Test
    public void scrollDownToRowWithDetails() {
        toggleDetailsFor(100);
        scrollToRow(100, ScrollDestination.ANY);

        Range validScrollRange = Range.between(1700, 1715);
        assertTrue(validScrollRange.contains(getGridVerticalScrollPos()));
    }

    @Test
    public void scrollUpToRowWithDetails() {
        toggleDetailsFor(100);
        scrollGridVerticallyTo(999999);
        scrollToRow(100, ScrollDestination.ANY);

        Range validScrollRange = Range.between(1990, 2010);
        assertTrue(validScrollRange.contains(getGridVerticalScrollPos()));
    }

    @Test
    public void cannotScrollBeforeTop() {
        toggleDetailsFor(1);
        scrollToRow(0, ScrollDestination.END);
        assertEquals(0, getGridVerticalScrollPos());
    }

    @Test
    public void cannotScrollAfterBottom() {
        toggleDetailsFor(999);
        scrollToRow(999, ScrollDestination.START);

        Range expectedRange = Range.withLength(19680, 20);
        assertTrue(expectedRange.contains(getGridVerticalScrollPos()));
    }

    private void scrollToRow(int rowIndex, ScrollDestination destination) {
        selectMenuPath(new String[] { "Component", "State", "Scroll to...",
                "Row " + rowIndex + "...", "Destination " + destination });
    }

    private void toggleDetailsFor(int rowIndex) {
        selectMenuPath(new String[] { "Component", "Row details",
                "Toggle details for...", "Row " + rowIndex });
    }
}
