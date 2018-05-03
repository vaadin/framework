package com.vaadin.tests.components.table;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.shared.ui.table.CollapseMenuContent;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class OnlyCollapsibleInMenu extends SingleBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return Tables.class;
    }

    @Test
    public void testOnlyCollapsibleInMenu() {
        openTestURL();
        CustomTableElement table = $(CustomTableElement.class).first();

        selectMenuPath("Component", "Columns", "Property 3", "Collapsible");
        table.getCollapseMenuToggle().click();
        Assert.assertEquals("Property 3 should still be in the context menu",
                "Property 3", table.getContextMenu().getItem(2).getText());

        selectMenuPath("Component", "Features", "Collapsible menu content",
                CollapseMenuContent.COLLAPSIBLE_COLUMNS.toString());
        table.getCollapseMenuToggle().click();
        Assert.assertEquals("Property 3 should not be in the context menu",
                "Property 4", table.getContextMenu().getItem(2).getText());

        selectMenuPath("Component", "Features", "Collapsible menu content",
                CollapseMenuContent.ALL_COLUMNS.toString());
        table.getCollapseMenuToggle().click();
        Assert.assertEquals("Property 3 should again  be in the context menu",
                "Property 3", table.getContextMenu().getItem(2).getText());
    }

}
