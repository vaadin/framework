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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableExpandRatioTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL();
    }

    /*
     * Needed for IE to get focus when button is clicked
     */
    @Override
    protected boolean requireWindowFocusForIE() {

        return true;
    }

    @Test
    public void cellWidthUpdatesWhenExpandRatioSetAfterDefinedWidth() {

        // test that after setting defined size to the second column, the first
        // column will have correct size

        setDefinedWidth();

        assertThat(getFirstCellWidth(), closeTo(500, 10));

        // test that after setting expandratio to the second column, it is
        // correct

        setExpandRatio();

        assertThat(getFirstCellWidth(), closeTo(65, 5));

    }

    private void setExpandRatio() {
        $(ButtonElement.class).id("expandbutton").click();
    }

    private void setDefinedWidth() {
        $(ButtonElement.class).id("widthbutton").click();
    }

    private double getFirstCellWidth() {

        List<WebElement> rows = $(TableElement.class).first()
                .findElement(By.className("v-table-body"))
                .findElements(By.tagName("tr"));
        WebElement firstrow = rows.get(0);
        List<WebElement> cells = firstrow.findElements(By
                .className("v-table-cell-content"));

        int cellwidth = cells.get(0).getSize().getWidth();
        return cellwidth;
    }
}
