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
package com.vaadin.tests.resources;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class FrontendLaterLoadedResourceUITest extends MultiBrowserTest {

    @Test
    public void correctEs5Es6FileImportedThroughFrontend() {
        openTestURL();
        $(ButtonElement.class).first().click();
        String es;
        if (BrowserUtil.isIE(getDesiredCapabilities())
                || BrowserUtil.isPhantomJS(getDesiredCapabilities())) {
            es = "es5";
        } else {
            es = "es6";
        }
        testBench().disableWaitForVaadin(); // For some reason needed by IE11

        Assert.assertEquals("/VAADIN/frontend/" + es + "/logFilename.js",
                findElement(By.tagName("body")).getText());
    }

}
