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

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.components.grid.basicfeatures.EscalatorBasicClientFeaturesTest;

/**
 * Test class to test the escalator level issue for ticket #16832
 */
public class EscalatorRemoveAndAddRowsTest extends
        EscalatorBasicClientFeaturesTest {

    @Test
    public void testRemoveAllRowsAndAddThirtyThenScroll() throws IOException {
        openTestURL();

        selectMenuPath(GENERAL, POPULATE_COLUMN_ROW);

        scrollVerticallyTo(99999);
        assertTrue("Escalator is not scrolled to bottom.",
                isElementPresent(By.xpath("//td[text() = 'Row 99: 0,99']")));

        selectMenuPath(COLUMNS_AND_ROWS, BODY_ROWS, REMOVE_ALL_INSERT_SCROLL);

        scrollVerticallyTo(99999);
        assertTrue("Escalator is not scrolled to bottom.",
                isElementPresent(By.xpath("//td[text() = 'Row 29: 0,129']")));
    }
}
