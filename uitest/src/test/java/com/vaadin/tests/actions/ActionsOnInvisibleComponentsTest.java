package com.vaadin.tests.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class ActionsOnInvisibleComponentsTest extends MultiBrowserTest {
    private static final String LAST_INIT_LOG = "3. 'C' triggers a click on a visible and enabled button";

    @Test
    public void testShortcutsOnInvisibleDisabledButtons() {
        openTestURL();
        assertEquals(LAST_INIT_LOG, getLogRow(0));
        invokeShortcut("a");
        assertEquals(LAST_INIT_LOG, getLogRow(0));
        invokeShortcut("b");
        assertEquals(LAST_INIT_LOG, getLogRow(0));
        invokeShortcut("c");
        assertEquals("4. Click event for enabled button", getLogRow(0));
    }

    private void invokeShortcut(CharSequence key) {
        new Actions(getDriver()).sendKeys(key).perform();
    }
}
