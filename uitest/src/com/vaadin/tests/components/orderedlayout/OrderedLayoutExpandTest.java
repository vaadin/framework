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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class OrderedLayoutExpandTest extends MultiBrowserTest {

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.tb3.MultiBrowserTest#getBrowsersToTest()
     */
    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        List<DesiredCapabilities> browsersToTest = super.getBrowsersToTest();

        // Not sure why, but IE11 might give the following error message:
        // "org.openqa.selenium.WebDriverException: Unexpected error launching
        // Internet Explorer. Browser zoom level was set to 75%. It should be
        // set to 100%".

        // setting "ignoreZoomSetting" capability to true seems to help, but if
        // it keeps bugging, just skip the IE11 completely bu uncommenting the
        // line below.
        // browsersToTest.remove(Browser.IE11.getDesiredCapabilities());
        Browser.IE11.getDesiredCapabilities().setCapability(
                "ignoreZoomSetting", true);

        // Can't test with IE8 with a zoom level other than 100%. IE8 could be
        // removed to speed up the test run.
        // browsersToTest.remove(Browser.IE8.getDesiredCapabilities());

        return browsersToTest;
    }

    @Test
    public void testNoAbortingLayoutAfter100PassesError() throws Exception {
        setDebug(true);
        openTestURL();

        if (!getDesiredCapabilities().equals(
                Browser.CHROME.getDesiredCapabilities())) {
            // Chrome uses css to to set zoom level to 110% that is known to
            // cause issues with the test app.
            // Other browsers tries to set browser's zoom level directly.
            WebElement html = driver.findElement(By.tagName("html"));
            // reset to 100% just in case
            html.sendKeys(Keys.chord(Keys.CONTROL, "0"));
            // zoom browser to 75% (ie) or 90% (FF). It depends on browser
            // how much "Ctrl + '-'" zooms out.
            html.sendKeys(Keys.chord(Keys.CONTROL, Keys.SUBTRACT));
        }

        // open debug window's Examine component hierarchy tab
        openDebugExamineComponentHierarchyTab();

        // click "Check layouts for potential problems" button
        clickDebugCheckLayoutsForPotentialProblems();

        // find div containing a successful layout analyze result
        WebElement pass = findLayoutAnalyzePassElement();
        // find div containing a error message with
        // "Aborting layout after 100 passess" message.
        WebElement error = findLayoutAnalyzeAbortedElement();

        Assert.assertNull(error);
        Assert.assertNotNull(pass);

        if (!getDesiredCapabilities().equals(
                Browser.CHROME.getDesiredCapabilities())) {
            WebElement html = driver.findElement(By.tagName("html"));
            // reset zoom level back to 100%
            html.sendKeys(Keys.chord(Keys.CONTROL, "0"));
        }
    }

    private void openDebugExamineComponentHierarchyTab() {
        WebElement button = findElement(By
                .xpath("//button[@title='Examine component hierarchy']"));
        // can't use 'click()' with zoom levels other than 100%
        button.sendKeys(Keys.chord(Keys.SPACE));
    }

    private void clickDebugCheckLayoutsForPotentialProblems() {
        WebElement button = findElement(By
                .xpath("//button[@title='Check layouts for potential problems']"));

        button.sendKeys(Keys.chord(Keys.SPACE));
    }

    private WebElement findLayoutAnalyzePassElement() {
        try {
            return findElement(By
                    .xpath("//div[text()='Layouts analyzed, no top level problems']"));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private WebElement findLayoutAnalyzeAbortedElement() {
        try {
            return findElement(By
                    .xpath("//div[text()='Aborting layout after 100 passess']"));
        } catch (NoSuchElementException e) {
            return null;
        }
    }
}
