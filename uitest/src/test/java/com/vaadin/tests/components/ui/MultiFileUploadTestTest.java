package com.vaadin.tests.components.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.UploadElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class MultiFileUploadTestTest extends MultiBrowserTest {

    @Test
    public void changeListenerWorksAfterFirstUpload() throws IOException {
        openTestURL();
        ButtonElement upload = $(ButtonElement.class).first();

        File tempFile = createTempFile();
        fillPathToUploadInput(tempFile.getPath(),
                $(UploadElement.class).last());
        tempFile = createTempFile();
        fillPathToUploadInput(tempFile.getPath(),
                $(UploadElement.class).last());
        tempFile = createTempFile();
        fillPathToUploadInput(tempFile.getPath(),
                $(UploadElement.class).last());

        assertEquals("Unexpected amount of Upload components.", 4,
                $(UploadElement.class).all().size());

        upload.click();

        // Last one doesn't have a file selected, and shouldn't trigger an
        // event.
        String logRow = getLogRow(0);
        assertTrue("Unexpected upload log: " + logRow,
                logRow.startsWith("3. Upload of ")
                        && logRow.endsWith(" complete"));
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

    private void fillPathToUploadInput(String tempFileName,
            UploadElement uploadElement) {
        // create a valid path in upload input element. Instead of selecting a
        // file by some file browsing dialog, we use the local path directly.
        WebElement input = getInput(uploadElement);
        setLocalFileDetector(input);
        input.sendKeys(tempFileName);
    }

    private WebElement getInput(UploadElement uploadElement) {
        return uploadElement.findElement(By.className("gwt-FileUpload"));
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
