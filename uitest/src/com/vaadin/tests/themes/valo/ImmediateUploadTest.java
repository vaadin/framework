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
package com.vaadin.tests.themes.valo;

import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.UploadElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to see if upload immediate mode hides the native file input.
 * 
 * @author Vaadin Ltd
 */
public class ImmediateUploadTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getAllBrowsers();
    }

    @Test
    public void fileInputShouldNotBeVisibleInImmediate()
            throws InterruptedException {
        openTestURL();

        UploadElement normalUpload = $(UploadElement.class).id("upload");
        UploadElement immediateUpload = $(UploadElement.class).id(
                "immediateupload");

        WebElement normalUploadInput = normalUpload.findElement(By
                .cssSelector("input[type='file']"));
        WebElement immediateUploadInput = immediateUpload.findElement(By
                .cssSelector("input[type='file']"));

        WebElement normalUploadButton = normalUpload.findElement(By
                .tagName("div"));
        WebElement immediateUploadButton = immediateUpload.findElement(By
                .tagName("div"));

        assertThat(normalUploadButton.getCssValue("display"),
                equalToIgnoringCase("block"));
        assertThat(immediateUploadButton.getCssValue("display"),
                equalToIgnoringCase("block"));

        assertThat(normalUploadInput.getCssValue("position"),
                equalToIgnoringCase("static"));
        assertThat(immediateUploadInput.getCssValue("position"),
                equalToIgnoringCase("absolute"));

    }
}
