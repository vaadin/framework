package com.vaadin.tests.components.menubar;

import static org.junit.Assert.assertEquals;

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

        // clicks separated to different calls for more informative errors
        menuBar.clickItem("File");
        menuBar.clickItem("Export..");
        menuBar.clickItem("As PDF...");
        assertEquals("1. MenuItem File/Export../As PDF... selected",
                getLogRow(0));

        menuBar.clickItem("Edit");
        menuBar.clickItem("Copy");
        assertEquals("2. MenuItem Edit/Copy selected", getLogRow(0));

        menuBar.clickItem("Help");
        assertEquals("3. MenuItem Help selected", getLogRow(0));

        menuBar.clickItem("File");
        menuBar.clickItem("Exit");
        assertEquals("4. MenuItem File/Exit selected", getLogRow(0));
    }
}
