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

import static com.vaadin.tests.components.table.TableSizeInTabsheet.TABLE;
import static com.vaadin.tests.components.table.TableSizeInTabsheet.TABSHEET;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableSizeInTabsheetTest extends MultiBrowserTest {

    private static final String TABSHEET_CONTENT_STYLENAME = "v-tabsheet-content";

    @Test
    public void testTabsheetContentHasTheSameHeightAsTable() {
        openTestURL();
        int tableHeight = getTableHeigth();
        int tabSheetContentHeight = getTableSheetContentHeight();

        Assert.assertEquals(tableHeight, tabSheetContentHeight);
    }

    private int getTableHeigth() {
        return vaadinElementById(TABLE).getSize().getHeight();
    }

    private int getTableSheetContentHeight() {
        WebElement tabsheetContent = vaadinElementById(TABSHEET).findElement(
                By.className(TABSHEET_CONTENT_STYLENAME));
        return tabsheetContent.getSize().getHeight();
    }
}
