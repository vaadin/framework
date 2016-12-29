package com.vaadin.tests.components.menubar;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class MenuBarNavigationMouseTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return MenuBarNavigation.class;
    }

    @Test
    public void testMenuBarMouseNavigation() throws Exception {
        openTestURL();
        MenuBarElement menuBar = $(MenuBarElement.class).first();
        menuBar.clickItem("File", "Export..", "As PDF...");
        Assert.assertEquals("1. MenuItem File/Export../As PDF... selected",
                getLogRow(0));
        menuBar.clickItem("Edit", "Copy");
        Assert.assertEquals("2. MenuItem Edit/Copy selected", getLogRow(0));
        menuBar.clickItem("Help");
        Assert.assertEquals("3. MenuItem Help selected", getLogRow(0));
        menuBar.clickItem("File", "Exit");
        Assert.assertEquals("4. MenuItem File/Exit selected", getLogRow(0));
    }
}
