package com.vaadin.tests.components.window;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CloseSubWindowTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        openSubWindow();
    }

    @Test
    public void testClosingFromClickHandler() throws Exception {
        $(WindowElement.class).$(ButtonElement.class).first().click();
        assertLogText();
    }

    @Test
    public void testClosingFromTitleBar() throws Exception {
        $(WindowElement.class).first()
                .findElement(By.className("v-window-closebox")).click();
        assertLogText();
    }

    @Test
    public void testClosingByRemovingFromUI() throws Exception {
        $(WindowElement.class).$(ButtonElement.class).get(1).click();
        assertLogText();
    }

    private void openSubWindow() {
        $(ButtonElement.class).id("opensub").click();
    }

    private void assertLogText() {
        Assert.assertEquals("Unexpected log contents,",
                "1. Window 'Sub-window' closed", getLogRow(0));
    }
}
