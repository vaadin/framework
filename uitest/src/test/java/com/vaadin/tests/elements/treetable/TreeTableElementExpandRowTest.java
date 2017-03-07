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
package com.vaadin.tests.elements.treetable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.TreeTableElement;
import com.vaadin.testbench.elements.TreeTableRowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TreeTableElementExpandRowTest extends MultiBrowserTest {
    TreeTableElement tree;

    @Before
    public void init() {
        openTestURL();
        tree = $(TreeTableElement.class).first();
    }

    @Test
    public void testGetRow() {
        testRowByIndex(1, "testValue", "");
    }

    @Test
    public void testExpandRow0() {
        TreeTableRowElement row = tree.getRow(0);
        row.toggleExpanded();// expand row
        testRowByIndex(1, "item1_1", "Should expand row with index 0.");
        testRowByIndex(2, "item1_2", "Should expand row with index 0.");

    }

    @Test
    public void testCollapseRow0() {
        TreeTableRowElement row = tree.getRow(0);
        row.toggleExpanded();// expand row
        testRowByIndex(1, "item1_1", "Should expand row with index 0.");
        row = tree.getRow(0);
        row.toggleExpanded(); // collapse row
        testRowByIndex(1, "testValue", "Should collapse row with index 0.");

    }

    private void testRowByIndex(int rowIndex, String expectedValue,
            String extraInfo) {
        TreeTableRowElement row = tree.getRow(rowIndex);
        WebElement cell = row.getCell(0);
        String errorMessage = "";
        if (extraInfo != null && !extraInfo.equals("")) {
            errorMessage += extraInfo;
        }
        errorMessage += "Return value of row=" + rowIndex + " cell=0 should be "
                + expectedValue + ".";
        Assert.assertEquals(errorMessage, expectedValue, cell.getText());
    }
}
