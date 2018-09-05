package com.vaadin.tests.elements.menubar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class MenuBarUITest extends MultiBrowserTest {

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    @Before
    public void init() {
        openTestURL();
    }

    // Tests against bug #14568
    @Test
    public void testClickTopLevelItemHavingSubmenuItemFocused() {
        MenuBarElement menuBar = $(MenuBarElement.class).first();

        menuBar.clickItem("File");
        assertTrue(isItemVisible("Export.."));

        menuBar.clickItem("Export..");
        assertTrue(isItemVisible("As PDF..."));

        menuBar.clickItem("File");
        assertFalse(isItemVisible("Export.."));
    }

    /**
     * Validates clickItem(String) of MenuBarElement.
     */
    @Test
    public void testMenuBarClick() {
        MenuBarElement menuBar = $(MenuBarElement.class).first();

        menuBar.clickItem("File");
        assertTrue(isItemVisible("Save As.."));

        menuBar.clickItem("Export..");
        assertTrue(isItemVisible("As PDF..."));

        // The Edit menu will be opened by moving the mouse over the item (done
        // by clickItem). The first click then actually closes the menu.
        menuBar.clickItem("Edit");
        menuBar.clickItem("Edit");
        assertFalse(isItemVisible("Save As.."));
        assertTrue(isItemVisible("Paste"));

        menuBar.clickItem("Edit");
        assertFalse(isItemVisible("Save As.."));
        assertFalse(isItemVisible("Paste"));

        menuBar.clickItem("Edit");
        assertFalse(isItemVisible("Save As.."));
        assertTrue(isItemVisible("Paste"));

        // Menu does not contain a submenu, no need to click twice.
        menuBar.clickItem("Help");
        assertFalse(isItemVisible("Save As.."));
        assertFalse(isItemVisible("Paste"));

        // No submenu is open, so click only once to open the File menu.
        menuBar.clickItem("File");
        assertTrue(isItemVisible("Save As.."));
    }

    /**
     * Validates menuBar.clickItem(String...) feature.
     */
    @Test
    public void testMenuBarClickPath() {
        MenuBarElement menuBar = $(MenuBarElement.class).first();
        menuBar.clickItem("File", "Export..");
        assertTrue(isItemVisible("As Doc..."));
    }

    /**
     * Tests whether the selected MenuBar and its items are the correct ones.
     */
    @Test
    public void testMenuBarSelector() {
        MenuBarElement menuBar = $(MenuBarElement.class).get(2);

        menuBar.clickItem("File");
        assertTrue(isItemVisible("Open2"));
        // Close the menu item
        menuBar.clickItem("File");

        menuBar = $(MenuBarElement.class).get(1);
        menuBar.clickItem("Edit2");
        assertTrue(isItemVisible("Cut"));
        menuBar.clickItem("Edit2");

        menuBar = $(MenuBarElement.class).first();
        menuBar.clickItem("File");
        assertTrue(isItemVisible("Open"));
    }

    @Test
    public void testMenuItemTooltips() {
        MenuBarElement first = $(MenuBarElement.class).first();
        first.clickItem("File");
        assertTooltip("Open", "<b>Preformatted</b>\ndescription");
        assertTooltip("Save", "plain description, <b>HTML</b> is visible");
        assertTooltip("Exit", "HTML\ndescription");
    }

    private void assertTooltip(String menuItem, String expectedTooltipText) {
        testBenchElement(getMenuElement(menuItem)).showTooltip();
        assertEquals("Unexpected tooltip when hovering '" + menuItem + "'",
                expectedTooltipText,
                findElement(By.className("v-tooltip-text")).getText());
    }

    private boolean isItemVisible(String item) {
        for (WebElement webElement : getItemCaptions()) {
            try {
                if (webElement.getText().equals(item)) {
                    return true;
                }
            } catch (WebDriverException e) {
                // stale, detached element is not visible
                return false;
            }
        }
        return false;
    }

    private List<WebElement> getItemCaptions() {
        return findElements(By.className("v-menubar-menuitem-caption"));
    }
}
