/*
 * Copyright 2000-2017 Vaadin Ltd.
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
package com.vaadin.test.osgi;

import static org.junit.Assert.assertEquals;

import java.util.function.Function;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.TextFieldElement;

public class KarafIntegrationIT extends TestBenchTestCase {

    private static final String URL_PREFIX = "http://localhost:8181/";
    private static final String APP1_URL = URL_PREFIX + "myapp1";
    private static final String APP2_URL = URL_PREFIX + "myapp2";

    @Test
    public void testApp1() {
        runBasicTest(APP1_URL, "bar");
        // App theme should make a button pink
        WebElement element = getDriver().findElement(By.className("v-button"));
        String buttonColor = element.getCssValue("color");
        assertEquals("rgba(255, 128, 128, 1)", buttonColor);
    }

    @Test
    public void testApp2() {
        runBasicTest(APP2_URL, "foo");
    }

    private void runBasicTest(String app1Url, String text) {
        getDriver().navigate().to(app1Url);
        new WebDriverWait(getDriver(), 5000)
                .until(driver -> isElementPresent(TextFieldElement.class));
        getDriver().findElement(By.className("v-textfield")).sendKeys(text);
        getDriver().findElement(By.className("v-button")).click();
        String foundText = getDriver().findElement(By.className("v-label"))
                .getText();
        assertEquals("Thanks " + text + ", it works!", foundText);
    }

    @Before
    public void setup() {
        setDriver(new PhantomJSDriver());
    }

    @After
    public void teardown() {
        getDriver().quit();
    }
}
