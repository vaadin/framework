package com.vaadin.tests.components.upload;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.util.LoremIpsum;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebElement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class InterruptUploadTest extends MultiBrowserTest {

    @Test
    public void testInterruptUpload() throws Exception {
        openTestURL();

        File tempFile = createTempFile();
        // Schedule upload cancel in a second
        ((JavascriptExecutor) getDriver()).executeScript(
                "setTimeout( function () {window.document.querySelector(\".v-window .v-button\").click()},2000)");

        fillPathToUploadInput(tempFile.getPath());

        // Wait for 3 seconds until everything is done.
        Thread.sleep(3000);

        String expected = " (counting interrupted at ";
        String actual = $(LabelElement.class).caption("Line breaks counted")
                .first().getText();
        assertTrue("Line break count note does not match expected (was: "
                + actual + ")", actual.contains(expected));

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
