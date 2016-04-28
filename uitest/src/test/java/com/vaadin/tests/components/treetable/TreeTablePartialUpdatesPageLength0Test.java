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
package com.vaadin.tests.components.treetable;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TreeTableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests expanding TreeTable rows when page length is zero.
 * 
 * @author Vaadin Ltd
 */
public class TreeTablePartialUpdatesPageLength0Test extends MultiBrowserTest {

    @Test
    public void testExpanding() throws IOException {
        openTestURL();

        TreeTableElement treeTable = $(TreeTableElement.class).first();
        List<WebElement> rows = treeTable.findElement(
                By.className("v-table-body")).findElements(By.tagName("tr"));
        assertEquals("unexpected row count", 4, rows.size());
        assertEquals("unexpected contents", "root1", treeTable.getCell(0, 0)
                .getText());
        assertEquals("unexpected contents", "root2", treeTable.getCell(1, 0)
                .getText());
        assertEquals("unexpected contents", "root3", treeTable.getCell(2, 0)
                .getText());
        assertEquals("unexpected contents", "END", treeTable.getCell(3, 0)
                .getText());

        // expand first row, should have 10 children
        treeTable.getCell(0, 0)
                .findElement(By.className("v-treetable-treespacer")).click();

        treeTable = $(TreeTableElement.class).first();
        rows = treeTable.findElement(By.className("v-table-body"))
                .findElements(By.tagName("tr"));
        assertEquals("unexpected row count", 14, rows.size());

        // expand root3, should have 200 children
        assertEquals("unexpected contents", "root3", treeTable.getCell(12, 0)
                .getText());
        treeTable.getCell(12, 0)
                .findElement(By.className("v-treetable-treespacer")).click();

        // expand root2, should have 200 children
        assertEquals("unexpected contents", "root2", treeTable.getCell(11, 0)
                .getText());
        treeTable.getCell(11, 0)
                .findElement(By.className("v-treetable-treespacer")).click();

        treeTable = $(TreeTableElement.class).first();
        rows = treeTable.findElement(By.className("v-table-body"))
                .findElements(By.tagName("tr"));
        assertEquals("unexpected row count", 414, rows.size());

        // scroll all the way to the bottom
        WebElement ui = findElement(By.className("v-ui"));
        testBenchElement(ui).scroll(12500);

        compareScreen("bottom");
    }
}
