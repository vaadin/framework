package com.vaadin.tests.components.menubar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class MenuBarNavigationKeyboardTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return com.vaadin.tests.components.menubar.MenuBarNavigation.class;
    }

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    @Override
    protected boolean usePersistentHoverForIE() {
        return false;
    }

    @Test
    public void testKeyboardNavigation() throws Exception {
        openTestURL();

        openMenu("File");
        getMenuBar().sendKeys(Keys.DOWN, Keys.DOWN, Keys.DOWN, Keys.DOWN,
                Keys.RIGHT, Keys.ENTER);
        assertEquals("1. MenuItem File/Export../As PDF... selected",
                getLogRow(0));

        openMenu("File");
        getMenuBar().sendKeys(Keys.RIGHT, Keys.RIGHT, Keys.RIGHT, Keys.ENTER);
        assertEquals("2. MenuItem Help selected", getLogRow(0));

        openMenu("Edit");
        getMenuBar().sendKeys(Keys.LEFT, Keys.DOWN, Keys.DOWN, Keys.ENTER);
        assertEquals("3. MenuItem Edit/Cut selected", getLogRow(0));

        openMenu("Edit");
        getMenuBar().sendKeys(Keys.ENTER);
        assertEquals("3. MenuItem Edit/Cut selected", getLogRow(0));

        getMenuBar().sendKeys(Keys.ENTER);
        assertEquals("4. MenuItem Edit/Copy selected", getLogRow(0));

        /* Enter while menubar has focus but no selection should focus "File" */
        getMenuBar().sendKeys(Keys.ENTER);
        assertEquals("4. MenuItem Edit/Copy selected", getLogRow(0));

        /* Enter again should open File and focus Open */
        getMenuBar().sendKeys(Keys.ENTER);
        assertEquals("4. MenuItem Edit/Copy selected", getLogRow(0));

        getMenuBar().sendKeys(Keys.ENTER);
        assertEquals("5. MenuItem File/Open selected", getLogRow(0));
    }

    @Test
    public void testMenuSelectWithKeyboardStateClearedCorrectly()
            throws InterruptedException {
        openTestURL();

        openMenu("File");

        getMenuBar().sendKeys(Keys.ARROW_RIGHT, Keys.ARROW_RIGHT,
                Keys.ARROW_RIGHT, Keys.ENTER);

        assertTrue("Help menu was not selected",
                logContainsText("MenuItem Help selected"));

        new Actions(driver).moveToElement(getMenuBar(), 10, 10).perform();

        assertFalse("Unexpected MenuBar popup is visible",
                isElementPresent(By.className("v-menubar-popup")));
    }

    @Test
    public void testNavigatingToDisabled() throws InterruptedException {
        openTestURL();

        openMenu("File");

        getMenuBar().sendKeys(Keys.ARROW_RIGHT, Keys.ARROW_RIGHT,
                Keys.ARROW_RIGHT, Keys.ARROW_RIGHT, Keys.ENTER);

        assertTrue("Disabled menu not selected",
                getFocusedElement().getText().contains("Disabled"));

        assertFalse("Disabled menu was triggered",
                logContainsText("MenuItem Disabled selected"));

        getMenuBar().sendKeys(Keys.ARROW_DOWN, Keys.ENTER);

        assertFalse("Disabled submenu was opened",
                logContainsText("MenuItem Disabled/Can't reach selected"));

        assertTrue("Disabled menu not selected",
                getFocusedElement().getText().contains("Disabled"));
    }

    public MenuBarElement getMenuBar() {
        return $(MenuBarElement.class).first();
    }

    public void openMenu(String name) {
        // move hover focus outside the MenuBar to keep the behaviour stable
        new Actions(driver).moveToElement($(LabelElement.class).first(), 10, 10)
                .perform();
        getMenuBar().clickItem(name);
    }
}
