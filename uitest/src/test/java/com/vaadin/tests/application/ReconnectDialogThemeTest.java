package com.vaadin.tests.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.CustomTestBenchCommandExecutor;
import com.vaadin.tests.tb3.MultiBrowserThemeTestWithProxy;

@TestCategory("communication")
public class ReconnectDialogThemeTest extends MultiBrowserThemeTestWithProxy {

    static By reconnectDialogBy = By.className("v-reconnect-dialog");

    @Test
    public void reconnectDialogTheme() throws IOException {
        openTestURL();
        ButtonElement helloButton = $(ButtonElement.class).caption("Say hello")
                .first();
        helloButton.click();
        assertEquals("1. Hello from the server", getLogRow(0));
        disconnectProxy();
        helloButton.click();
        testBench().disableWaitForVaadin();
        waitUntil(driver -> isElementPresent(reconnectDialogBy));

        WebElement dialog = findElement(reconnectDialogBy);
        WebElement spinner = dialog.findElement(By.className("spinner"));

        // Hide spinner to make screenshot stable
        executeScript("arguments[0].style.visibility='hidden';", spinner);
        compareScreen("onscreen-without-spinner");

        // Show spinner and make sure it is shown by comparing to the screenshot
        // without a spinner
        executeScript("arguments[0].style.visibility='visible';", spinner);
        BufferedImage fullScreen = ImageIO
                .read(new ByteArrayInputStream(((TakesScreenshot) getDriver())
                        .getScreenshotAs(OutputType.BYTES)));
        BufferedImage spinnerImage = CustomTestBenchCommandExecutor
                .cropToElement(spinner, fullScreen);
        assertHasManyColors("Spinner is not shown", spinnerImage);

    }

    @Test
    public void gaveUpTheme() throws IOException {
        openTestURL("reconnectAttempts=3");

        waitUntil(input -> {
            try {
                return $(ButtonElement.class).first() != null;
            } catch (Exception e) {
                return false;
            }
        });

        disconnectProxy();
        $(ButtonElement.class).first().click();

        waitForReconnectDialogWithText("Server connection lost.");
        compareScreen("gaveupdialog");

    }

    private void waitForReconnectDialogWithText(final String text) {
        waitUntil(input -> {
            try {
                final WebElement reconnectDialog = findElement(
                        ReconnectDialogThemeTest.reconnectDialogBy);
                return reconnectDialog.findElement(By.className("text"))
                        .getText().equals(text);
            } catch (Exception e) {
                return false;
            }
        }, 10);
    }

    private void assertHasManyColors(String message,
            BufferedImage spinnerImage) {
        int backgroundColor = spinnerImage.getRGB(0, 0);
        for (int x = 0; x < spinnerImage.getWidth(); x++) {
            for (int y = 0; y < spinnerImage.getHeight(); y++) {
                if (Math.abs(
                        spinnerImage.getRGB(x, y) - backgroundColor) > 50) {
                    return;
                }
            }
        }
        fail(message);

    }

    @Override
    protected Class<?> getUIClass() {
        return ReconnectDialogUI.class;
    }

}
