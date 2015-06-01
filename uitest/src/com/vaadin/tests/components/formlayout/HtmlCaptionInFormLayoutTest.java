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
package com.vaadin.tests.components.formlayout;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class HtmlCaptionInFormLayoutTest extends SingleBrowserTest {
    @Test
    public void testHtmlCaptions() {
        openTestURL();

        List<WebElement> captions = getDriver().findElements(
                By.cssSelector(".v-formlayout-captioncell span"));

        Assert.assertEquals("Should be two formlayout captions", 2,
                captions.size());

        Assert.assertEquals("Contains HTML", captions.get(0).getText());
        Assert.assertEquals("Contains <b>HTML</b>", captions.get(1).getText());
    }

    @Test
    public void testHtmlCaptionToggle() {
        openTestURL();

        $(ButtonElement.class).caption("Toggle").first().click();

        List<WebElement> captions = getDriver().findElements(
                By.cssSelector(".v-formlayout-captioncell span"));

        Assert.assertEquals("Should be two formlayout captions", 2,
                captions.size());

        Assert.assertEquals("Contains <b>HTML</b>", captions.get(0).getText());
        Assert.assertEquals("Contains HTML", captions.get(1).getText());
    }

}
