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
package com.vaadin.tests.components;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class SaneErrorsTest extends MultiBrowserTest {

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.tb3.MultiBrowserTest#getBrowsersToTest()
     */
    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowserCapabilities(Browser.FIREFOX);
    }

    @Test
    public void test() {
        openTestURL();
        List<WebElement> elements = getDriver().findElements(
                By.xpath("//*[text() = 'Show me my NPE!']"));
        for (WebElement webElement : elements) {
            webElement.click();
        }

        getDriver().findElement(By.xpath("//*[text() = 'Collect exceptions']"))
                .click();

        List<WebElement> errorMessages = getDriver().findElements(
                By.className("v-label"));
        for (WebElement webElement : errorMessages) {
            String text = webElement.getText();
            Assert.assertEquals("java.lang.NullPointerException", text);
        }
    }

}
