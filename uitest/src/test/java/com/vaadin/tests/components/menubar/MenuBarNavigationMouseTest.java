package com.vaadin.tests.components.menubar;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class MenuBarNavigationMouseTest extends MultiBrowserTest {

    private LabelElement label;

    @Override
    protected Class<?> getUIClass() {
        return MenuBarNavigation.class;
    }

    @Test
    public void testMenuBarMouseNavigation() throws Exception {
        openTestURL();
        MenuBarElement menuBar = $(MenuBarElement.class).first();
        label = $(LabelElement.class).first();

        // move to Label to ensure all mouse moves are treated the same
        resetMousePosition();

        // clicks separated to different calls for more informative errors
        menuBar.clickItem("File");
        menuBar.clickItem("Export..");
        menuBar.clickItem("As PDF...");
        assertEquals("1. MenuItem File/Export../As PDF... selected",
                getLogRow(0));

        resetMousePosition();

        menuBar.clickItem("Edit");
        menuBar.clickItem("Copy");
        assertEquals("2. MenuItem Edit/Copy selected", getLogRow(0));

        resetMousePosition();

        menuBar.clickItem("Help");
        assertEquals("3. MenuItem Help selected", getLogRow(0));

        resetMousePosition();

        menuBar.clickItem("File");
        menuBar.clickItem("Exit");
        assertEquals("4. MenuItem File/Exit selected", getLogRow(0));
    }

    private void resetMousePosition() {
        new Actions(driver).moveToElement(label).perform();
    }
}
