/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.components.grid;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridColumnShrinkSmallerThanContentsTest extends SingleBrowserTest {

    @Test
    public void scrollbarAndNoScrollbar() {
        openTestURL();
        GridElement noshrinkColumnGrid = $(GridElement.class).get(0);
        GridElement shrinkColumnGrid = $(GridElement.class).get(1);
        assertHorizontalScrollbar(noshrinkColumnGrid.getHorizontalScroller(),
                "Should have a horizontal scrollbar as column 2 should be wide");
        assertNoHorizontalScrollbar(shrinkColumnGrid.getHorizontalScroller(),
                "Should not have a horizontal scrollbar as column 2 should be narrow");
    }
}
