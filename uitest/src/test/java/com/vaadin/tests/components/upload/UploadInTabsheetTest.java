package com.vaadin.tests.components.upload;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebElement;

import com.vaadin.testbench.elements.UploadElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Verifies that there's no client side errors when changing a tab containing
 * Upload right after uploading is succeeded (#8728)
 */
public class UploadInTabsheetTest extends MultiBrowserTest {

    @Test
    public void testThatChangingTabAfterUploadDoesntCauseErrors()
            throws Exception {
        setDebug(true);
        openTestURL();

        File tempFile = createTempFile();
        fillPathToUploadInput(tempFile.getPath());

        getSubmitButton().click();

        assertNoErrorNotifications();
    }

    /**
     * @return The generated temp file handle
     * @throws IOException
     */
    private File createTempFile() throws IOException {
        File tempFile = Files.createTempFile("TestFileUpload", ".txt").toFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        writer.write(getTempFileContents());
        writer.close();
        tempFile.deleteOnExit();
        return tempFile;
    }

    private String getTempFileContents() {
        return "This is a test file!\nRow 2\nRow3";
    }

    private void fillPathToUploadInput(String tempFileName) throws Exception {
        // create a valid path in upload input element. Instead of selecting a
        // file by some file browsing dialog, we use the local path directly.
        WebElement input = getInput();
        setLocalFileDetector(input);
        input.sendKeys(tempFileName);
    }

    private WebElement getSubmitButton() {
        UploadElement upload = $(UploadElement.class).first();
        WebElement submitButton = upload.findElement(By.className("v-button"));
        return submitButton;
    }

    private WebElement getInput() {
        return getDriver().findElement(By.className("gwt-FileUpload"));
    }

    private void setLocalFileDetector(WebElement element) throws Exception {
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
