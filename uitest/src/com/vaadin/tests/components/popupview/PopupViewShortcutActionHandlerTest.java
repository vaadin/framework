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
package com.vaadin.tests.components.popupview;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.TestBench;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Check availability of shortcut action listener in the popup view.
 * 
 * @author Vaadin Ltd
 */
public class PopupViewShortcutActionHandlerTest extends MultiBrowserTest {

    @Test
    public void testShortcutHandling() {
        openTestURL();

        getDriver().findElement(By.className("v-popupview")).click();
        WebElement textField = getDriver().findElement(
                By.className("v-textfield"));
        textField.sendKeys("a", Keys.ENTER);

        Assert.assertTrue(
                "Unable to find label component which is the result of"
                        + " shortcut action handling.",
                isElementPresent(By.className("shortcut-result")));
    }

    @Override
    protected void setupDriver() throws Exception {
        System.setProperty("phantomjs.binary.path",
                "C:\\tmp\\phantom\\phantomjs.exe");
        WebDriver dr = TestBench.createDriver(new PhantomJSDriver());
        setDriver(dr);
    }

    @Override
    protected String getScreenshotDirectory() {
        return "C:\\tmp\\a";
    }

    @Override
    protected void openTestURL() {
        driver.get("http://localhost:8080/vaadin/run/PopupViewShortcutActionHandler");
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return Collections.singletonList(Browser.FIREFOX
                .getDesiredCapabilities());
    }

}
