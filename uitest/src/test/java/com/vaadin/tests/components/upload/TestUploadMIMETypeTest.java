package com.vaadin.tests.components.upload;

import com.vaadin.testbench.elements.UploadElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebElement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static com.vaadin.tests.components.upload.TestUploadMIMEType.TEST_MIME_TYPE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestUploadMIMETypeTest extends MultiBrowserTest {
    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    }

    @Test
    public void testAcceptAttribute() throws Exception {
        WebElement input = getInput();
        assertThat(input.getAttribute("accept"), is(TEST_MIME_TYPE));
        uploadFile();
        waitUntil(driver -> getSubmitButton().isEnabled());
        //Previous element is removed, getting a new one
        input = getInput();
        assertThat(
                String.format("Accept is expected to be %s , but was %s ",
                        TEST_MIME_TYPE, input.getAttribute("accept")),
                input.getAttribute("accept"), is(TEST_MIME_TYPE));}

    private void uploadFile() throws Exception {
        File tempFile = createTempFile();
        fillPathToUploadInput(tempFile.getPath());
        getSubmitButton().click();
    }

    /**
     * @return The generated temp file handle
     * @throws IOException
     */
    private File createTempFile() throws IOException {
        File tempFile = Files.createTempFile("TestFileUpload", ".pdf").toFile();
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
    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // IE11 throws an `Unhandled Alert Exception`
        //https://stackoverflow.com/questions/23883071/unhandled-alert-exception-modal-dialog-present-selenium
        return getBrowserCapabilities(Browser.CHROME, Browser.FIREFOX);
    }
}
