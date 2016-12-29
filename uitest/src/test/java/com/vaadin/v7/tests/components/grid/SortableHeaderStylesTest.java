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
package com.vaadin.v7.tests.components.grid;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.OptionGroupElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class SortableHeaderStylesTest extends SingleBrowserTest {
    @Test
    public void testSortableHeaderStyles() {
        openTestURL();

        Assert.assertFalse(hasSortableStyle(0));
        for (int i = 1; i < 8; i++) {
            Assert.assertTrue(hasSortableStyle(i));
        }

        OptionGroupElement sortableSelector = $(OptionGroupElement.class)
                .first();

        // Toggle sortability
        sortableSelector.selectByText("lastName");
        Assert.assertFalse(hasSortableStyle(3));

        // Toggle back
        sortableSelector.selectByText("lastName");
        Assert.assertTrue(hasSortableStyle(3));
    }

    private boolean hasSortableStyle(int column) {
        return $(GridElement.class).first().getHeaderCell(0, column)
                .getAttribute("class").contains("sortable");
    }
}
