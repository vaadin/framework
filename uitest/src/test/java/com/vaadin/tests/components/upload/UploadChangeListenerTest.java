package com.vaadin.tests.components.upload;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class UploadChangeListenerTest extends MultiBrowserTest {

    @Test
    public void changeListenerWorksAfterFirstUpload() throws IOException {
        openTestURL();
        WebElement upload = findElement(By.className("v-button"));

        File tempFile = createTempFile();
        fillPathToUploadInput(tempFile.getPath());

        assertEquals("1. change", getLogRow(0));

        upload.click();

        assertEquals("2. finished", getLogRow(0));

        tempFile = createTempFile();
        fillPathToUploadInput(tempFile.getPath());

        assertEquals("3. change", getLogRow(0));
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
        StringBuilder sb = new StringBuilder("This is a small test file.");
        sb.append("\n");
        sb.append("Very small.");
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
