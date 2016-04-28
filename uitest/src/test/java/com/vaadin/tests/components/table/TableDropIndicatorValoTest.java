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

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that clicking on active fields doesn't change Table selection, nor does
 * dragging rows.
 * 
 * @author Vaadin Ltd
 */

public class TableDropIndicatorValoTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {

        super.setup();
        openTestURL();
    }

    @Test
    public void indicator() throws Exception {

        dragRowWithoutDropping(1);
        compareScreen("indicator");
    }

    private List<WebElement> getCellContents(WebElement row) {

        return row.findElements(By.className("v-table-cell-content"));
    }

    private List<WebElement> getRows() {

        return getTable().findElement(By.className("v-table-body"))
                .findElements(By.tagName("tr"));
    }

    private TableElement getTable() {

        return $(TableElement.class).first();
    }

    private void dragRowWithoutDropping(int from) {

        List<WebElement> rows = getRows();
        WebElement row = rows.get(from);
        List<WebElement> cellContents = getCellContents(row);

        int rowHeight = row.getSize().getHeight();
        int halfRowHeight = (int) (rowHeight + 0.5) / 2; // rounded off
        int oneAndAHalfRow = rowHeight + halfRowHeight;

        new Actions(getDriver()).moveToElement(cellContents.get(1))
                .clickAndHold().moveByOffset(0, oneAndAHalfRow).perform();
    }
}