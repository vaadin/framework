package com.vaadin.tests.components.menubar;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elementsbase.ServerClass;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class MenuBarNavigationMouseTest extends MultiBrowserTest {

    @ServerClass("com.vaadin.ui.MenuBar")
    public static class MenuBarElement extends
            com.vaadin.testbench.elements.MenuBarElement {

        public void openMenuPath(String... captions) {
            for (String c : captions) {
                findElement(By.vaadin("#" + c)).click();
            }
        }
    }

    @Override
    protected Class<?> getUIClass() {
        return MenuBarNavigation.class;
    }

    @Test
    public void testMenuBarMouseNavigation() throws Exception {
        openTestURL();
        MenuBarElement menuBar = $(MenuBarElement.class).first();
        menuBar.openMenuPath("File", "Export..", "As PDF...");
        Assert.assertEquals("1. MenuItem File/Export../As PDF... selected",
                getLogRow(0));
        menuBar.openMenuPath("Edit", "Copy");
        Assert.assertEquals("2. MenuItem Edit/Copy selected", getLogRow(0));
        menuBar.openMenuPath("Help");
        Assert.assertEquals("3. MenuItem Help selected", getLogRow(0));
        menuBar.openMenuPath("File", "Exit");
        Assert.assertEquals("4. MenuItem File/Exit selected", getLogRow(0));
    }
}
