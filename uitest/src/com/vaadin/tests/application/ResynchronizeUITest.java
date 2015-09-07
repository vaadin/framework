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
package com.vaadin.tests.application;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ResynchronizeUITest extends SingleBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // PhantomJS does not send onload events for styles
        return Collections.singletonList(Browser.FIREFOX
                .getDesiredCapabilities());
    }

    @Test
    public void ensureResynchronizeRecreatesDOM() {
        openTestURL();
        ButtonElement button = $(ButtonElement.class).first();
        button.click();
        // Click causes repaint, after this the old button element should no
        // longer be available
        // Ensure that the theme has changed
        waitForThemeToChange("runo");
        try {
            button.click();
            Assert.fail("The old button element should have been removed by the click and replaced by a new one.");
        } catch (StaleElementReferenceException e) {
            // This is what should happen
        }
    }
}
