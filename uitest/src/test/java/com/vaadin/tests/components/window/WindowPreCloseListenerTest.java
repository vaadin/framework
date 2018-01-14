package com.vaadin.tests.components.window;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WindowPreCloseListenerTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        openSubWindow();
    }

    @Test
    public void testClosingFromServer() {
        $(WindowElement.class).$(ButtonElement.class).first().click();
        assertEquals("Unexpected log contents,",
                "1. Window 'Sub-window' closed", getLogRow(0));
    }

    @Test
    public void testClosingFromTitleBar() {
        $(WindowElement.class).first()
                .findElement(By.className("v-window-closebox")).click();
        assertLogText();
        assertTrue($(WindowElement.class).exists());
    }

    @Test
    public void testClosingByShortcut() {
        $(WindowElement.class).first()
                .findElement(By.className("v-scrollable")).sendKeys(Keys.ESCAPE);

        assertLogText();
        assertTrue($(WindowElement.class).exists());
    }

    private void openSubWindow() {
        $(ButtonElement.class).id("opensub").click();
    }

    private void assertLogText() {
        assertEquals("Unexpected log contents,",
                "1. Window 'Sub-window' close attempt prevented", getLogRow(0));
    }
}
