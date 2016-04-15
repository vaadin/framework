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

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.TreeTableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that opening the root node and clicking a generated component doesn't
 * cause out of sync (or any other system notifications).
 * 
 * @author Vaadin Ltd
 */
public class TreeTableOutOfSyncTest extends MultiBrowserTest {

    @Test
    public void testNotification() throws InterruptedException {
        openTestURL();

        TreeTableElement treeTable = $(TreeTableElement.class).first();
        List<WebElement> rows = treeTable.findElement(
                By.className("v-table-body")).findElements(By.tagName("tr"));

        WebElement treeSpacer = rows.get(0).findElement(
                By.className("v-treetable-treespacer"));
        treeSpacer.click();

        sleep(100);

        rows = treeTable.findElement(By.className("v-table-body"))
                .findElements(By.tagName("tr"));
        WebElement button = rows.get(2).findElement(By.className("v-button"));
        button.click();

        List<WebElement> notifications = findElements(By
                .className("v-Notification-system"));
        assertTrue(notifications.isEmpty());
    }

}
