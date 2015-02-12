/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.upload;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebElement;

import com.vaadin.testbench.elements.UploadElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TestFileUploadTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // PhantomJS fails to upload files for unknown reasons
        return getBrowsersExcludingPhantomJS();
    }

    @Test
    public void testUploadAnyFile() throws Exception {
        openTestURL();

        File tempFile = createTempFile();
        fillPathToUploadInput(tempFile.getPath());

        getSubmitButton().click();

        String expected = String.format(
                "1. Upload finished. Name: %s, Size: %s, md5: %s",
                tempFile.getName(), getTempFileContents().length(),
                md5(getTempFileContents()));

        String actual = getLogRow(0);
        Assert.assertEquals("Upload log row does not match expected", expected,
                actual);
    }

    private String md5(String string) throws NoSuchAlgorithmException {
        byte[] digest = MessageDigest.getInstance("MD5").digest(
                string.getBytes());
        BigInteger bigInt = new BigInteger(1, digest);
        String hashtext = bigInt.toString(16);
        return hashtext;
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
