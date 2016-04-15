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
package com.vaadin.tests.components.window;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import com.vaadin.tests.tb3.newelements.WindowElement;

public class WindowCaptionTest extends SingleBrowserTest {

    private WindowElement htmlWindow;
    private WindowElement textWindow;

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        waitForElementPresent(By.className("v-window"));
        htmlWindow = $(WindowElement.class).id("htmlWindow");
        textWindow = $(WindowElement.class).id("textWindow");
    }

    @Test
    public void htmlCaption() {
        Assert.assertEquals("HtmlWindow's caption didn't match,",
                "This may or may not be red", htmlWindow.getCaption());
        Assert.assertEquals("TextWindow's caption didn't match,",
                "<font style='color: red;'>This may or may not be red</font>",
                textWindow.getCaption());
    }

    @Test
    public void textCaption() {
        clickButton("Plain text");
        ensureCaptionsEqual("This is just text");
    }

    @Test
    public void nullCaption() {
        clickButton("Null");
        ensureCaptionsEqual("");
    }

    @Test
    public void emptyCaption() {
        clickButton("Empty");
        ensureCaptionsEqual("");
    }

    private void clickButton(String caption) {
        $(ButtonElement.class).caption(caption).first().click();
    }

    private void ensureCaptionsEqual(final String expectedCaption) {
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return expectedCaption.equals(htmlWindow.getCaption());
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return "htmlWindow's caption to be '" + expectedCaption
                        + "' (was: '" + htmlWindow.getCaption() + "')";
            }

        });

        Assert.assertEquals("TextWindow's caption didn't match,",
                expectedCaption, textWindow.getCaption());
    }
}
