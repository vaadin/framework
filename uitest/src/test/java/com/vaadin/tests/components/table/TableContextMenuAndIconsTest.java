package com.vaadin.tests.components.table;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableContextMenuAndIconsTest extends MultiBrowserTest {
    @Override
    protected Class<?> getUIClass() {
        return com.vaadin.tests.components.table.Tables.class;
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingContextMenu();
    }

    @Test
    public void tableContextMenuWithIcons() throws Exception {
        openTestURL();
        /* Hide event log */
        selectMenuPath("Settings", "Show event log");
        /* Simple context menu */
        selectMenuPath("Component", "Features", "Context menu",
                "Item without icon");
        contextClickCell(1, 1);
        compareScreen("contextmenu-noicon");
        /* Two actions, without and with icon */
        selectMenuPath("Component", "Features", "Context menu",
                "With and without icon");
        contextClickCell(4, 2);
        compareScreen("caption-only-and-has-icon");
        /* Large icon */
        selectMenuPath("Component", "Features", "Context menu",
                "Only one large icon");
        contextClickCell(4, 2);
        compareScreen("large-icon");
        /*
         * Simple context menu again to ensure it is properly updated (icons
         * removed)
         */
        selectMenuPath("Component", "Features", "Context menu",
                "Item without icon");
        contextClickCell(1, 1);
        compareScreen("contextmenu-noicon");
        /* Empty context menu */
        selectMenuPath("Component", "Features", "Context menu", "Empty");
        contextClickCell(3, 3);
        compareScreen("contextmenu-empty");
    }

    private void contextClickCell(int row, int column) {
        TestBenchElement cell = $(TableElement.class).first().getCell(row,
                column);
        new Actions(driver).contextClick(cell).perform();
    }

}
