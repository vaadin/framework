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
package com.vaadin.tests.components.grid.basicfeatures.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeatures;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class GridDetailsServerTest extends GridBasicFeaturesTest {
    /**
     * The reason to why last item details wasn't selected is that since it will
     * exist only after the viewport has been scrolled into view, we wouldn't be
     * able to scroll that particular details row into view, making tests
     * awkward with two scroll commands back to back.
     */
    private static final int ALMOST_LAST_ITEM_INDEX = GridBasicFeatures.ROWS - 5;
    private static final String[] ALMOST_LAST_ITEM_DETAILS = new String[] {
            "Component", "Details", "lastItemId-5" };

    private static final String[] FIRST_ITEM_DETAILS = new String[] {
            "Component", "Details", "firstItemId" };
    private static final String[] TOGGLE_FIRST_ITEM_DETAILS = new String[] {
            "Component", "Details", "toggle firstItemId" };
    private static final String[] CUSTOM_DETAILS_GENERATOR = new String[] {
            "Component", "Details", "custom details generator" };
    private static final String[] HIERARCHY_DETAILS_GENERATOR = new String[] {
            "Component", "Details", "hierarchy details generator" };
    private static final String[] CHANGE_HIERARCHY = new String[] {
            "Component", "Details", "change hierarchy in generator" };

    @Before
    public void setUp() {
        openTestURL();
    }

    @Test
    public void openVisibleDetails() {
        try {
            getGridElement().getDetails(0);
            fail("Expected NoSuchElementException");
        } catch (NoSuchElementException ignore) {
            // expected
        }
        selectMenuPath(FIRST_ITEM_DETAILS);
        assertNotNull("details should've opened", getGridElement()
                .getDetails(0));
    }

    @Test(expected = NoSuchElementException.class)
    public void closeVisibleDetails() {
        selectMenuPath(FIRST_ITEM_DETAILS);
        selectMenuPath(FIRST_ITEM_DETAILS);

        getGridElement().getDetails(0);
    }

    @Test
    public void openDetailsOutsideOfActiveRange() {
        getGridElement().scroll(10000);
        selectMenuPath(FIRST_ITEM_DETAILS);
        getGridElement().scroll(0);
        assertNotNull("details should've been opened", getGridElement()
                .getDetails(0));
    }

    @Test(expected = NoSuchElementException.class)
    public void closeDetailsOutsideOfActiveRange() {
        selectMenuPath(FIRST_ITEM_DETAILS);
        getGridElement().scroll(10000);
        selectMenuPath(FIRST_ITEM_DETAILS);
        getGridElement().scroll(0);
        getGridElement().getDetails(0);
    }

    @Test
    public void componentIsVisibleClientSide() {
        selectMenuPath(CUSTOM_DETAILS_GENERATOR);
        selectMenuPath(FIRST_ITEM_DETAILS);

        TestBenchElement details = getGridElement().getDetails(0);
        assertNotNull("No widget detected inside details",
                details.findElement(By.className("v-widget")));
    }

    @Test
    public void togglingAVisibleDetailsRowWithSeparateRoundtrips() {
        selectMenuPath(CUSTOM_DETAILS_GENERATOR);
        selectMenuPath(FIRST_ITEM_DETAILS); // open
        selectMenuPath(FIRST_ITEM_DETAILS); // close
        selectMenuPath(FIRST_ITEM_DETAILS); // open

        TestBenchElement details = getGridElement().getDetails(0);
        assertNotNull("No widget detected inside details",
                details.findElement(By.className("v-widget")));
    }

    @Test(expected = NoSuchElementException.class)
    public void scrollingDoesNotCreateAFloodOfDetailsRows() {
        selectMenuPath(CUSTOM_DETAILS_GENERATOR);

        // scroll somewhere to hit uncached rows
        getGridElement().scrollToRow(101);

        // this should throw
        getGridElement().getDetails(100);
    }

    @Test
    public void openingDetailsOutOfView() {
        getGridElement().scrollToRow(500);

        selectMenuPath(CUSTOM_DETAILS_GENERATOR);
        selectMenuPath(FIRST_ITEM_DETAILS);

        getGridElement().scrollToRow(0);

        // if this fails, it'll fail before the assertNotNull
        assertNotNull("unexpected null details row", getGridElement()
                .getDetails(0));
    }

    @Test
    public void togglingAVisibleDetailsRowWithOneRoundtrip() {
        selectMenuPath(CUSTOM_DETAILS_GENERATOR);
        selectMenuPath(FIRST_ITEM_DETAILS); // open

        assertTrue("Unexpected generator content",
                getGridElement().getDetails(0).getText().endsWith("(0)"));
        selectMenuPath(TOGGLE_FIRST_ITEM_DETAILS);
        assertTrue("New component was not displayed in the client",
                getGridElement().getDetails(0).getText().endsWith("(1)"));
    }

    @Test
    @Ignore("This will be patched with https://dev.vaadin.com/review/#/c/7917/")
    public void almosLastItemIdIsRendered() {
        selectMenuPath(CUSTOM_DETAILS_GENERATOR);
        selectMenuPath(ALMOST_LAST_ITEM_DETAILS);
        scrollGridVerticallyTo(100000);

        TestBenchElement details = getGridElement().getDetails(
                ALMOST_LAST_ITEM_INDEX);
        assertNotNull(details);
        assertTrue("Unexpected details content",
                details.getText().endsWith(ALMOST_LAST_ITEM_INDEX + " (0)"));
    }

    @Test
    public void hierarchyChangesWorkInDetails() {
        selectMenuPath(HIERARCHY_DETAILS_GENERATOR);
        selectMenuPath(FIRST_ITEM_DETAILS);
        assertEquals("One", getGridElement().getDetails(0).getText());
        selectMenuPath(CHANGE_HIERARCHY);
        assertEquals("Two", getGridElement().getDetails(0).getText());
    }

    @Test
    @Ignore("This will be patched with https://dev.vaadin.com/review/#/c/7917/")
    public void hierarchyChangesWorkInDetailsWhileOutOfView() {
        selectMenuPath(HIERARCHY_DETAILS_GENERATOR);
        selectMenuPath(FIRST_ITEM_DETAILS);
        scrollGridVerticallyTo(10000);
        selectMenuPath(CHANGE_HIERARCHY);
        scrollGridVerticallyTo(0);
        assertEquals("Two", getGridElement().getDetails(0).getText());
    }
}
