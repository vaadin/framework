package com.vaadin.tests.components.ui;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class WindowAndUIShortcutsTest extends SingleBrowserTest {

    @Test
    public void windowShortcutShouldNotReachUI() {
        openTestURL();
        $(ButtonElement.class).caption("Show page").first().click();
        $(ButtonElement.class).caption("Open dialog window").first().click();

        WindowElement window = $(WindowElement.class).first();
        // for PhantomJS to have the focus in the right place
        window.click();
        window.$(TextFieldElement.class).first().sendKeys(Keys.ESCAPE);

        // Window should have been closed
        assertTrue($(WindowElement.class).all().isEmpty());
        // "Close page" should not have been clicked
        assertTrue($(ButtonElement.class).caption("Close page").exists());
    }

    @Test
    public void modalCurtainShouldNotTriggerShortcuts() {
        openTestURL();
        $(ButtonElement.class).caption("Show page").first().click();
        $(ButtonElement.class).caption("Open dialog window").first().click();

        WebElement curtain = findElement(
                By.className("v-window-modalitycurtain"));
        curtain.sendKeys(Keys.ESCAPE);
        // "Close page" should not have been clicked
        assertTrue($(ButtonElement.class).caption("Close page").exists());

    }
}
