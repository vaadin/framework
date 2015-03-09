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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;

import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

@RunLocally(Browser.PHANTOMJS)
public class GridDetailsServerTest extends GridBasicFeaturesTest {
    private static final String[] FIRST_ITEM_DETAILS = new String[] {
            "Component", "Details", "firstItemId" };

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
}
