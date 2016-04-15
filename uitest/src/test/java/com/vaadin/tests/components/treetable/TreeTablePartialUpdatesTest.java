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
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.commands.TestBenchElementCommands;
import com.vaadin.testbench.elements.TreeTableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests partial updates of a TreeTable.
 * 
 * @author Vaadin Ltd
 */
public class TreeTablePartialUpdatesTest extends MultiBrowserTest {

    @Test
    public void testLongScroll() throws IOException, InterruptedException {
        openTestURL();

        TreeTableElement treeTable = $(TreeTableElement.class).first();
        List<WebElement> rows = treeTable.findElement(
                By.className("v-table-body")).findElements(By.tagName("tr"));
        assertEquals("unexpected row count", 4, rows.size());

        // expand the first root element
        treeTable.getCell(0, 0)
                .findElement(By.className("v-treetable-treespacer")).click();
        treeTable = $(TreeTableElement.class).first();

        // wait for the scrollposition element to disappear
        waitUntilNot(ExpectedConditions.visibilityOfElementLocated(By
                .className("v-table-scrollposition")));

        rows = treeTable.findElement(By.className("v-table-body"))
                .findElements(By.tagName("tr"));
        assertEquals("unexpected cached row count", 46, rows.size());

        // TODO: replace these with just treeTable.scroll(int) when #13826 has
        // been fixed
        TestBenchElementCommands scrollable = testBenchElement(treeTable
                .findElement(By.className("v-scrollable")));

        // scroll far enough down to drop the first row from the cache
        // but not far enough to reach the last row
        scrollable.scroll(1692);

        // wait for the scrollposition element to disappear
        waitUntilNot(ExpectedConditions.visibilityOfElementLocated(By
                .className("v-table-scrollposition")));

        assertEquals("elements found where there should be none", 0, treeTable
                .findElements(By.vaadin("#row[0]/col[0]")).size());
        assertEquals("elements found where there should be none", 0, treeTable
                .findElements(By.vaadin("#row[203]/col[0]")).size());

        // scroll 6000 to make sure to actually hit bottom
        scrollable.scroll(6000);

        // wait for the scrollposition element to disappear
        waitUntilNot(ExpectedConditions.visibilityOfElementLocated(By
                .className("v-table-scrollposition")));

        // check the contents
        treeTable = $(TreeTableElement.class).first();
        rows = treeTable.findElement(By.className("v-table-body"))
                .findElements(By.tagName("tr"));
        assertEquals("unexpected cached row count", 45, rows.size());
        assertEquals("unexpected cell contents (final row expected)", "END",
                treeTable.getCell(203, 0).getText());
        assertEquals("unexpected cell contents (first visible row expected)",
                "188", treeTable.getCell(189, 0).getText());
        assertEquals("unexpected cell contents (first cached row expected)",
                "158", treeTable.getCell(159, 0).getText());

        assertEquals("elements found where there should be none", 0, treeTable
                .findElements(By.vaadin("#row[158]/col[0]")).size());
        assertEquals("elements found where there should be none", 0, treeTable
                .findElements(By.vaadin("#row[204]/col[0]")).size());

        // check the actual visibility
        compareScreen("bottom");
    }

    @Test
    public void testNegativeArraySize() throws IOException,
            InterruptedException {
        openTestURL();

        TreeTableElement treeTable = $(TreeTableElement.class).first();
        List<WebElement> rows = treeTable.findElement(
                By.className("v-table-body")).findElements(By.tagName("tr"));
        assertEquals("unexpected row count", 4, rows.size());

        // expand the first root element
        treeTable.getCell(0, 0)
                .findElement(By.className("v-treetable-treespacer")).click();

        // wait for the scrollposition element to disappear
        waitUntilNot(ExpectedConditions.visibilityOfElementLocated(By
                .className("v-table-scrollposition")));

        treeTable = $(TreeTableElement.class).first();
        rows = treeTable.findElement(By.className("v-table-body"))
                .findElements(By.tagName("tr"));
        assertEquals("unexpected cached row count", 46, rows.size());

        // TODO: replace these with just treeTable.scroll(int) when #13826 has
        // been fixed
        TestBenchElementCommands scrollable = testBenchElement(treeTable
                .findElement(By.className("v-scrollable")));

        // scroll far enough down to reach the second root item again
        scrollable.scroll(3969);

        // wait for the scrollposition element to disappear
        waitUntilNot(ExpectedConditions.visibilityOfElementLocated(By
                .className("v-table-scrollposition")));

        assertEquals("elements found where there should be none", 0, treeTable
                .findElements(By.vaadin("#row[0]/col[0]")).size());
        assertEquals("unexpected cell contents", "root2",
                treeTable.getCell(201, 0).getText());
        assertEquals("unexpected cell contents", "END",
                treeTable.getCell(203, 0).getText());

        // expand the second root element
        treeTable.getCell(201, 0)
                .findElement(By.className("v-treetable-treespacer")).click();

        // wait for the scrollposition element to disappear
        waitUntilNot(ExpectedConditions.visibilityOfElementLocated(By
                .className("v-table-scrollposition")));

        // ensure the last cached row isn't the final row
        treeTable = $(TreeTableElement.class).first();
        rows = treeTable.findElement(By.className("v-table-body"))
                .findElements(By.tagName("tr"));
        String elementText = rows.get(rows.size() - 1)
                .findElement(By.className("v-table-cell-wrapper")).getText();
        assertFalse("final row found when it should be beyond cache",
                elementText.contains("END"));

        // collapse the second root element
        treeTable.getCell(201, 0)
                .findElement(By.className("v-treetable-treespacer")).click();

        // wait for the scrollposition element to disappear
        waitUntilNot(ExpectedConditions.visibilityOfElementLocated(By
                .className("v-table-scrollposition")));

        // check the contents
        treeTable = $(TreeTableElement.class).first();
        rows = treeTable.findElement(By.className("v-table-body"))
                .findElements(By.tagName("tr"));
        assertEquals("unexpected cached row count", 45, rows.size());
        assertEquals("unexpected cell contents (final row expected)", "END",
                treeTable.getCell(203, 0).getText());
        assertEquals("unexpected cell contents (first visible row expected)",
                "188", treeTable.getCell(189, 0).getText());
        assertEquals("unexpected cell contents (first cached row expected)",
                "158", treeTable.getCell(159, 0).getText());

        assertEquals("elements found where there should be none", 0, treeTable
                .findElements(By.vaadin("#row[158]/col[0]")).size());
        assertEquals("elements found where there should be none", 0, treeTable
                .findElements(By.vaadin("#row[204]/col[0]")).size());

        // check the actual visibility
        compareScreen("bottom");
    }

}
