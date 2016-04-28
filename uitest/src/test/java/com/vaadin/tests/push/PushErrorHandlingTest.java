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
package com.vaadin.tests.push;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("push")
public class PushErrorHandlingTest extends MultiBrowserTest {

    @Test
    public void testErrorHandling() {
        setPush(true);
        openTestURL();
        vaadinElementById("npeButton").click();
        int idx = 1;
        if (BrowserUtil.isPhantomJS(getDesiredCapabilities())) {
            // PhantomJS sends an extra event when page gets loaded.
            // This results as an extra error label.
            ++idx;
        }
        Assert.assertEquals(
                "An error! Unable to invoke method click in com.vaadin.shared.ui.button.ButtonServerRpc",
                $(LabelElement.class).get(idx).getText());

        WebElement table = vaadinElementById("testtable");
        WebElement row = table.findElement(By
                .xpath("//div[text()='Click for NPE']"));
        row.click();

        Assert.assertEquals("Internal error",
                vaadinElement("Root/VNotification[0]/HTML[0]/domChild[0]")
                        .getText());
    }
}
