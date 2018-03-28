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
        GridCellElement deceased = grid.getHeaderCell(0, 2);

        lastName.showTooltip();
        Assert.assertEquals("HTML: Header tooltip for last name",
                getTooltipElement().getText());

        firstName.showTooltip();
        Assert.assertEquals("Text: Header tooltip for <b>first</b> name",
                getTooltipElement().getText());

        deceased.showTooltip();
        Assert.assertEquals("PRE\nHeader tooltip for\n<b>deceased</b>",
                getTooltipElement().getText());
    }

    @Test
    public void headerWithoutTooltipShowsGridTooltip() {
        GridCellElement otherHeader = grid.getHeaderCell(0, 3);

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
        GridCellElement deceased = grid.getFooterCell(0, 2);

        lastName.showTooltip();
        Assert.assertEquals("HTML: Footer tooltip for last name",
                getTooltipElement().getText());

        firstName.showTooltip();
        Assert.assertEquals("Text: Footer tooltip for <b>first</b> name",
                getTooltipElement().getText());

        deceased.showTooltip();
        Assert.assertEquals("PRE\nFooter tooltip for\n<b>deceased</b>",
                getTooltipElement().getText());

    }
}
