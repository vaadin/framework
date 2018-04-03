package com.vaadin.v7.tests.components.tree;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TreeContextMenuAndIconsTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return Trees.class;
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingContextMenu();
    }

    @Test
    public void testSimpleContextMenu() throws Exception {
        openTestURL();

        selectMenuPath("Settings", "Show event log");
        selectMenuPath("Component", "Features", "Context menu",
                "Item without icon");

        openContextMenu(getTreeNodeByCaption("Item 1"));

        compareScreen("contextmenu-noicon");

        closeContextMenu();
    }

    @Test
    public void testContextMenuWithAndWithoutIcon() throws Exception {
        openTestURL();

        selectMenuPath("Settings", "Show event log");
        selectMenuPath("Component", "Features", "Context menu",
                "With and without icon");

        openContextMenu(getTreeNodeByCaption("Item 1"));

        compareScreen("caption-only-and-has-icon");

        closeContextMenu();
    }

    @Test
    public void testContextLargeIcon() throws Exception {
        openTestURL();

        selectMenuPath("Settings", "Show event log");
        selectMenuPath("Component", "Features", "Context menu",
                "Only one large icon");

        WebElement menu = openContextMenu(getTreeNodeByCaption("Item 1"));

        // reindeer doesn't support menu with larger row height, so the
        // background image contains parts of other sprites =>
        // just check that the menu is of correct size
        Dimension size = menu.getSize();
        assertEquals("Menu height with large icons", 74, size.height);

        closeContextMenu();
    }

    @Test
    public void testContextRemoveIcon() throws Exception {
        openTestURL();

        selectMenuPath("Settings", "Show event log");
        selectMenuPath("Component", "Features", "Context menu",
                "Only one large icon");

        openContextMenu(getTreeNodeByCaption("Item 1"));
        closeContextMenu();

        selectMenuPath("Component", "Features", "Context menu",
                "Item without icon");

        openContextMenu(getTreeNodeByCaption("Item 1"));

        compareScreen("contextmenu-noicon");

        closeContextMenu();
    }

    private WebElement openContextMenu(WebElement element) {
        Actions actions = new Actions(getDriver());
        // Note: on Firefox, the first menu item does not get focus; on other
        // browsers it does
        actions.contextClick(element);
        actions.perform();
        return findElement(By.className("v-contextmenu"));
    }

    private void closeContextMenu() {
        findElement(By.className("v-app")).click();
    }

    private WebElement getTreeNodeByCaption(String caption) {
        return getDriver()
                .findElement(By.xpath("//span[text() = '" + caption + "']"));
    }

}
