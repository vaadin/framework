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
package com.vaadin.tests.components.abstractfield;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ShortcutWhenBodyFocusedTest extends SingleBrowserTest {

    @Test
    public void triggerShortcutOnBody() {
        openTestURL();
        ButtonElement b = $(ButtonElement.class).caption("Hello").first();
        b.click();
        Assert.assertEquals("1. Hello clicked", getLogRow(0));

        b.sendKeys("A");
        Assert.assertEquals("2. Hello clicked", getLogRow(0));

        WebElement body = findElement(By.xpath("//body"));
        body.sendKeys("A");
        Assert.assertEquals("3. Hello clicked", getLogRow(0));
    }
}
