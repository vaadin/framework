/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.requesthandlers;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.LinkElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class AppResource404Test extends MultiBrowserTest {
    @Test
    public void testOpenExistingResource() throws Exception {
        openTestURL();
        $(LinkElement.class).first().click(5, 5);
        disableWaitingAndWait();
        Assert.assertFalse("Page contains the given text",
                driver.getPageSource().contains("404"));
    }

    @Test
    public void testOpenNonExistingResource() {
        openTestURL();
        $(LinkElement.class).get(1).click(5, 5);
        disableWaitingAndWait();
        Assert.assertTrue("Page does not contain the given text",
                driver.getPageSource().contains(
                        "/APP/connector/0/4/asdfasdf can not be found"));
    }

    @Test
    public void testOpenResourceWith404() {
        openTestURL();
        $(LinkElement.class).get(2).click(5, 5);
        disableWaitingAndWait();
        Assert.assertTrue("Page does not contain the given text",
                driver.getPageSource().contains("HTTP ERROR 404"));
        Assert.assertTrue("Page does not contain the given text",
                driver.getPageSource().contains("Problem accessing /run/APP/"));
    }

    @Test
    public void testOpenResourceToUIProvider() {
        openTestURL();
        $(LinkElement.class).get(3).click(5, 5);
        disableWaitingAndWait();
        Assert.assertFalse("Page contains the given text",
                driver.getPageSource().contains("can not be found"));
    }

    protected void disableWaitingAndWait() {
        testBench().disableWaitForVaadin();
        sleep(500);
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // IE11 does not show the details on the 404 page
        return super.getBrowsersExcludingIE();
    }
}
