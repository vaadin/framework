/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.extensions;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class IframeIsOpenedInNonIOSTest extends MultiBrowserTest {

    @Test
    public void fileOpenedInNewTab() {
        openTestURL();

        $(ButtonElement.class).caption("Download").first().click();

        List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
        boolean containsFileIframe = false;
        for (WebElement iframe : iframes) {
            containsFileIframe = containsFileIframe | iframe.getAttribute("src")
                    .contains(IframeIsOpenedInNonIOS.FILE_NAME);
        }

        Assert.assertTrue("page doesn't contain iframe with the file",
                containsFileIframe);
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // once running ios is possible, this test should be fixed to exclude it
        // from the browsers list

        // The test is failing in all IEs for some reason even though the iframe
        // is in place.
        // Probably related to some IE driver issue
        return getBrowsersExcludingIE();
    }
}
