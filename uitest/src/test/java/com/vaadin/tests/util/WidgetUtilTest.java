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
package com.vaadin.tests.util;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.widgetset.server.WidgetUtilUI;

public class WidgetUtilTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        List<DesiredCapabilities> l = super.getBrowsersToTest();
        // IE8 does not support getComputedStyle
        l.remove(Browser.IE8.getDesiredCapabilities());
        return l;
    }

    @Test
    public void testBlockElementRequiredSizeComputedStyle() {
        openTestURL();
        WebElement testComponent = findElement(By
                .className("v-widget-util-test"));
        testComponent.click();

        int padding = (int) Math.ceil(2.4 + 3.5);
        int border = (int) Math.ceil(1.8 * 2);
        int baseWidth = 300;
        int baseHeight = 50;

        if (BrowserUtil.isPhantomJS(getDesiredCapabilities())
                && getDesiredCapabilities().getVersion().equals("1")) {
            // PhantomJS1 rounds padding to integers
            padding = 2 + 3;
        }

        if (browserRoundsBorderToInteger(getDesiredCapabilities())) {
            border = 1 * 2;
        }

        assertExpectedSize(testComponent, "noBorderPadding", baseWidth + "x"
                + baseHeight);

        assertExpectedSize(testComponent, "border", (baseWidth + border) + "x"
                + (baseHeight + border));

        assertExpectedSize(testComponent, "padding", (baseWidth + padding)
                + "x" + (baseHeight + padding));

        assertExpectedSize(testComponent, "borderPadding",
                (baseWidth + border + padding) + "x"
                        + (baseHeight + border + padding));

    }

    private void assertExpectedSize(WebElement testComponent, String id,
            String size) {
        WebElement e = testComponent.findElement(By.id(id));
        Assert.assertEquals(id + ": " + size, e.getText());
    }

    private boolean browserRoundsBorderToInteger(
            DesiredCapabilities capabilities) {
        // Note that this is how the Windows browsers in the test cluster work.
        // On Mac, Firefox works slightly differently (rounds border to 1.5px).
        return (BrowserUtil.isChrome(capabilities)
                || BrowserUtil.isPhantomJS(capabilities) || BrowserUtil
                    .isFirefox(capabilities));
    }

    @Override
    protected Class<?> getUIClass() {
        return WidgetUtilUI.class;
    }
}
