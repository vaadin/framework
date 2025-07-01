package com.vaadin.tests.components.upload;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.UploadElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import com.vaadin.tests.util.LoremIpsum;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DisablingUploadTest extends SingleBrowserTest {

    ButtonElement button;
    ButtonElement pushButton;
    ButtonElement stateButton;

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL();
    }

    @Test
    public void buttonWorksAsExpected() {
        buttonGroup();

        // Disable button is working
        assertTrue("Upload button should be enabled",
                getSubmitButton().isEnabled());
        button.click();
        assertFalse("Upload button should be disabled",
                getSubmitButton().isEnabled());

        // pushmode button is working
        assertEquals("Set the Push Mode", pushButton.getCaption());
        pushButton.click();
        sleep(100);
        assertEquals("enable push mode", pushButton.getCaption());
        pushButton.click();
        sleep(100);
        assertEquals("disable push mode", pushButton.getCaption());

        // upload button state is correct
        assertEquals("true", stateButton.getCaption());
        stateButton.click();
        sleep(100);
        assertEquals("false", stateButton.getCaption());
    }

    @Test
    public void pushEnabled_uploadFile_uploadButtonDisabled() throws Exception {
        buttonGroup();

        uploadFile(false);

        String expected = "2. File has been uploaded.";

        String actual = getLogRow(0);
        assertEquals("Upload log row does not match expected", expected,
                actual);

        stateButton.click();
        sleep(100);
        assertEquals("false", stateButton.getCaption());

        uploadFile(false);
        // assert no new log
        assertEquals("Upload log row does not match expected", expected,
                actual);
    }

    @Test
    public void pushDisabled_uploadFile_uploadButtonDisabled()
            throws Exception {
        buttonGroup();

        pushButton.click();

        uploadFile(false);

        String expected = "2. File has been uploaded.";

        String actual = getLogRow(0);
        assertEquals("Upload log row does not match expected", expected,
                actual);

        stateButton.click();
        sleep(100);
        assertEquals("false", stateButton.getCaption());

        uploadFile(false);
        // assert no new log
        assertEquals("Upload log row does not match expected", expected,
                actual);
    }

    @Test
    public void pushEnabled_uploadLargeFile_uploadButtonDisabled()
            throws Exception {
        buttonGroup();

        uploadFile(true);

        String expected = "2. File has been uploaded.";

        String actual = getLogRow(0);
        assertEquals("Upload log row does not match expected", expected,
                actual);

        stateButton.click();
        sleep(100);
        assertEquals("false", stateButton.getCaption());

        uploadFile(true);
        // assert no new log
        assertEquals("Upload log row does not match expected", expected,
                actual);
    }

    @Test
    public void pushDisabled_uploadLargeFile_uploadButtonDisabled()
            throws Exception {
        buttonGroup();

        pushButton.click();

        uploadFile(true);

        String expected = "2. File has been uploaded.";

        String actual = getLogRow(0);
        assertEquals("Upload log row does not match expected", expected,
                actual);

        stateButton.click();
        sleep(100);
        assertEquals("false", stateButton.getCaption());

        uploadFile(true);
        // assert no new log
        assertEquals("Upload log row does not match expected", expected,
                actual);
    }

    private void buttonGroup() {
        button = $(ButtonElement.class).id("button-id");
        pushButton = $(ButtonElement.class).id("push-button");
        stateButton = $(ButtonElement.class).id("state-button");
    }

    private void uploadFile(boolean large) throws Exception {
        File tempFile = createTempFile(large);
        fillPathToUploadInput(tempFile.getPath());

        getSubmitButton().click();
        sleep(100);
    }

    /**
     * @return The generated temp file handle
     * @throws IOException
     */
    private File createTempFile(boolean large) throws IOException {
        File tempFile = Files.createTempFile("TestFileUpload", ".txt").toFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        if (large) {
            writer.write(getLargeTempFileContents());
        } else {
            writer.write(getTempFileContents());
        }
        writer.close();
        tempFile.deleteOnExit();
        return tempFile;
    }

    private String getTempFileContents() {
        return "This is a test file!\nRow 2\nRow3";
    }

    private String getLargeTempFileContents() {
        return LoremIpsum.get();
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
