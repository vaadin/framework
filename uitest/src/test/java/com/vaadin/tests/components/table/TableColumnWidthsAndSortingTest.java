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
package com.vaadin.tests.components.table;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableColumnWidthsAndSortingTest extends MultiBrowserTest {

    @Test
    public void testHeaderHeight() {
        openTestURL();
        TableElement t = $(TableElement.class).first();

        assertHeaderCellHeight(t);

        // Sort according to age
        t.getHeaderCell(2).click();
        assertHeaderCellHeight(t);

        // Sort again according to age
        t.getHeaderCell(2).click();
        assertHeaderCellHeight(t);

    }

    private void assertHeaderCellHeight(TableElement t) {
        // Assert all headers are correct height (37px according to default
        // Valo)
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals("Height of header cell " + i + " is wrong", 37,
                    t.getHeaderCell(0).getSize().getHeight());
        }

    }
}
