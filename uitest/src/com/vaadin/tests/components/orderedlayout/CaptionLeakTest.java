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
package com.vaadin.tests.components.orderedlayout;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class CaptionLeakTest extends MultiBrowserTest {

    @Test
    public void testCaptionLeak() throws Exception {
        setDebug(true);
        openTestURL();

        // this should be present
        // 3 general non-connector elements, none accumulated on click
        getDriver()
                .findElement(
                        By.xpath("//span[text() = 'Measured 3 non connector elements']"));

        getDriver().findElement(By.xpath("//button[@title = 'Clear log']"))
                .click();
        getDriver().findElement(By.id("Set leaky content")).click();

        getDriver()
                .findElement(
                        By.xpath("//span[text() = 'Measured 3 non connector elements']"));

        // nothing accumulates over clicks
        getDriver().findElement(By.xpath("//button[@title = 'Clear log']"))
                .click();
        getDriver().findElement(By.id("Set leaky content")).click();
        getDriver()
                .findElement(
                        By.xpath("//span[text() = 'Measured 3 non connector elements']"));
    }

    @Test
    public void testNoCaptionLeak() throws Exception {
        setDebug(true);
        openTestURL();

        getDriver().findElement(By.xpath("//button[@title = 'Clear log']"))
                .click();
        getDriver().findElement(By.id("Set non leaky content")).click();

        // this should be present
        // 3 general non-connector elements, none accumulated on click
        getDriver()
                .findElement(
                        By.xpath("//span[text() = 'Measured 3 non connector elements']"));
    }
}
