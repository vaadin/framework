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

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.TreeTableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TreeTableScrollOnExpandTest extends MultiBrowserTest {

    @Test
    public void testScrollOnExpand() throws InterruptedException, IOException {
        openTestURL();
        TreeTableElement tt = $(TreeTableElement.class).first();
        tt.getRow(0).click();
        tt.scroll(300);
        sleep(1000);
        tt.getRow(20).toggleExpanded();
        // Need to wait a bit to avoid accepting the case where the TreeTable is
        // in the desired state only for a short while.
        sleep(1000);
        WebElement focusedRow = getDriver().findElement(
                By.className("v-table-focus"));
        assertEquals("Item 21", focusedRow.getText());
    }
}