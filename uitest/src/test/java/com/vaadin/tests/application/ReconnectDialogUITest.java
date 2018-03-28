package com.vaadin.tests.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.jcraft.jsch.JSchException;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTestWithProxy;

@TestCategory("needs-ssh")
public class ReconnectDialogUITest extends MultiBrowserTestWithProxy {

    @Test
    public void reconnectTogglesBodyStyle() throws JSchException {
        openTestURL();
        getButton().click();
        disconnectProxy();
        getButton().click();
        waitForReconnectDialogPresent();
        WebElement body = findElement(By.xpath("//body"));
        assertTrue("Body should have a style name when reconnecting",
                hasCssClass(body, "v-reconnecting"));
        connectProxy();
        waitForReconnectDialogToDisappear();
        assertFalse("Body should no longer have a style name when reconnected",
                hasCssClass(body, "v-reconnecting"));
    }

    @Test
    public void reconnectDialogShownAndDisappears() throws JSchException {
        openTestURL();
        getButton().click();
        assertEquals("1. Hello from the server", getLogRow(0));
        disconnectProxy();
        getButton().click();
        waitForReconnectDialogWithText(
                "Server connection lost, trying to reconnect...");
        connectProxy();
        waitForReconnectDialogToDisappear();
        assertEquals("2. Hello from the server", getLogRow(0));
    }

    private void waitForReconnectDialogWithText(final String text) {
        waitForReconnectDialogPresent();
        final WebElement reconnectDialog = findElement(
                ReconnectDialogThemeTest.reconnectDialogBy);
        waitUntil(input -> reconnectDialog.findElement(By.className("text"))
                .getText().equals(text), 10);
    }

    private void waitForReconnectDialogToDisappear() {
        waitForElementNotPresent(ReconnectDialogThemeTest.reconnectDialogBy);

    }

    private void waitForReconnectDialogPresent() {
        waitForElementPresent(ReconnectDialogThemeTest.reconnectDialogBy);
    }

    private WebElement getButton() {
        return $(ButtonElement.class).first();
    }

}
