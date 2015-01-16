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
package com.vaadin.tests.components.grid.basicfeatures.escalator;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.components.grid.basicfeatures.EscalatorBasicClientFeaturesTest;

@SuppressWarnings("all")
public class EscalatorScrollTest extends EscalatorBasicClientFeaturesTest {

    @Before
    public void setUp() {
        openTestURL();
        populate();
    }

    /**
     * Before the fix, removing and adding rows and also scrolling would put the
     * scroll state in an internally inconsistent state. The scrollbar would've
     * been scrolled correctly, but the body wasn't.
     * 
     * This was due to optimizations that didn't keep up with the promises, so
     * to say. So the optimizations were removed.
     */
    @Test
    public void testScrollRaceCondition() {
        scrollVerticallyTo(40);
        String originalStyle = getTBodyStyle();
        selectMenuPath(COLUMNS_AND_ROWS, BODY_ROWS, REMOVE_ALL_INSERT_SCROLL);

        // body should be scrolled to exactly the same spot. (not 0)
        assertEquals(originalStyle, getTBodyStyle());
    }

    @Test
    public void scrollToBottomAndRemoveHeader() throws Exception {
        scrollVerticallyTo(999999); // to bottom

        /*
         * apparently the scroll event isn't fired by the time the next assert
         * would've been done.
         */
        Thread.sleep(50);

        assertEquals("Unexpected last row cell before header removal",
                "Row 99: 0,99", getBodyCell(-1, 0).getText());
        selectMenuPath(COLUMNS_AND_ROWS, HEADER_ROWS,
                REMOVE_ONE_ROW_FROM_BEGINNING);
        assertEquals("Unexpected last row cell after header removal",
                "Row 99: 0,99", getBodyCell(-1, 0).getText());

    }

    @Test
    public void scrollToBottomAndRemoveFooter() throws Exception {
        scrollVerticallyTo(999999); // to bottom

        /*
         * apparently the scroll event isn't fired by the time the next assert
         * would've been done.
         */
        Thread.sleep(50);

        assertEquals("Unexpected last row cell before footer removal",
                "Row 99: 0,99", getBodyCell(-1, 0).getText());
        selectMenuPath(COLUMNS_AND_ROWS, FOOTER_ROWS,
                REMOVE_ONE_ROW_FROM_BEGINNING);
        assertEquals("Unexpected last row cell after footer removal",
                "Row 99: 0,99", getBodyCell(-1, 0).getText());
    }

    private String getTBodyStyle() {
        WebElement tbody = getEscalator().findElement(By.tagName("tbody"));
        return tbody.getAttribute("style");
    }
}
