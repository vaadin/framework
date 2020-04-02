package com.vaadin.tests.components.upload;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.util.LoremIpsum;

public class InterruptUploadTest extends MultiBrowserTest {

    private static final String EXPECTED_COUNTER_TEXT = " (counting interrupted at ";

    @Test
    public void testInterruptUpload() throws Exception {
        openTestURL();

        File tempFile = createTempFile();
        fillPathToUploadInput(tempFile.getPath());

        waitForElementPresent(By.className("v-window"));
        $(ButtonElement.class).caption("Cancel").first().click();
        waitUntilInterruptionRegistered();

        $(WindowElement.class).first().close();
        waitForElementNotPresent(By.className("v-window"));

        // Check if second upload happens
        tempFile = createTempFile();
        fillPathToUploadInput(tempFile.getPath());

        waitForElementPresent(By.className("v-window"));
        $(ButtonElement.class).caption("Cancel").first().click();
        waitUntilInterruptionRegistered();
    }

    private void waitUntilInterruptionRegistered() {
        waitUntil(new ExpectedCondition<Boolean>() {
            String actual;

            @Override
            public Boolean apply(WebDriver arg0) {
                actual = $(LabelElement.class).caption("Line breaks counted")
                        .first().getText();
                return actual.contains(EXPECTED_COUNTER_TEXT);
            }

            @Override
            public String toString() {
                // Expected condition failed: waiting for ...
                return "line break count note to mention interruption (was: "
                        + actual + ")";
            }
        });
    }

    /**
     * @return The generated temp file handle
     * @throws IOException
     */
    private File createTempFile() throws IOException {
        File tempFile = File.createTempFile("TestFileUpload", ".txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        writer.write(getTempFileContents());
        writer.close();
        tempFile.deleteOnExit();
        return tempFile;
    }

    private String getTempFileContents() {
        StringBuilder sb = new StringBuilder("This is a big test file!");
        for (int i = 0; i < 70; ++i) {
            sb.append("\n");
            sb.append(LoremIpsum.get());
        }
        return sb.toString();
    }

    private void fillPathToUploadInput(String tempFileName) {
        // create a valid path in upload input element. Instead of selecting a
        // file by some file browsing dialog, we use the local path directly.
        WebElement input = getInput();
        setLocalFileDetector(input);
        input.sendKeys(tempFileName);
    }

    private WebElement getInput() {
        return getDriver().findElement(By.className("gwt-FileUpload"));
    }

    private void setLocalFileDetector(WebElement element) {
        if (getRunLocallyBrowser() != null) {
            return;
        }

        if (element instanceof WrapsElement) {
            element = ((WrapsElement) element).getWrappedElement();
        }
        if (element instanceof RemoteWebElement) {
            ((RemoteWebElement) element)
                    .setFileDetector(new LocalFileDetector());
        } else {
            throw new IllegalArgumentException(
                    "Expected argument of type RemoteWebElement, received "
                            + element.getClass().getName());
        }
    }

}
