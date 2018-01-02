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
package com.vaadin.tests.components.grid;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridHeaderFooterTooltipTest extends SingleBrowserTest {

    private GridElement grid;

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        grid = $(GridElement.class).first();
    }

    @Test
    public void headerTooltipShown() {
        GridCellElement lastName = grid.getHeaderCell(0, 0);
        GridCellElement firstName = grid.getHeaderCell(0, 1);

        lastName.showTooltip();
        Assert.assertEquals("Header tooltip for last name",
                getTooltipElement().getText());

        firstName.showTooltip();
        Assert.assertEquals("Header tooltip for <b>first</b> name",
                getTooltipElement().getText());
    }

    @Test
    public void headerWithoutTooltipShowsGridTooltip() {
        GridCellElement otherHeader = grid.getHeaderCell(0, 2);

        otherHeader.showTooltip();
        Assert.assertEquals("Tooltip for the whole grid",
                getTooltipElement().getText());

    }

    @Test
    public void joinedHeaderTooltipShown() {
        $(ButtonElement.class).id("join").click();
        GridCellElement fullName = grid.getHeaderCell(0, 0);
        fullName.showTooltip();
        Assert.assertEquals("Full name tooltip", getTooltipElement().getText());
    }

    @Test
    public void footerTooltipShown() {
        GridCellElement lastName = grid.getFooterCell(0, 0);
        GridCellElement firstName = grid.getFooterCell(0, 1);

        lastName.showTooltip();
        Assert.assertEquals("Footer tooltip for last name",
                getTooltipElement().getText());

        firstName.showTooltip();
        Assert.assertEquals("Footer tooltip for <b>first</b> name",
                getTooltipElement().getText());

    }
}
