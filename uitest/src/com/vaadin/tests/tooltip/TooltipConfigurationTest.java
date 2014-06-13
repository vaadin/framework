/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.tests.tooltip;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.TooltipTest;

public class TooltipConfigurationTest extends TooltipTest {

    @Test
    public void testTooltipConfiguration() throws Exception {
        openTestURL();

        WebElement uiRoot = getDriver().findElement(By.vaadin("Root"));
        WebElement closeTimeout = vaadinElementById("Close timeout");
        WebElement shortTooltip = vaadinElementById("shortTooltip");
        WebElement longTooltip = vaadinElementById("longTooltip");
        WebElement maxWidth = vaadinElementById("Max width");

        selectAndType(closeTimeout, "0");

        checkTooltip(shortTooltip, "This is a short tooltip");

        moveToRoot();

        checkTooltipNotPresent();

        selectAndType(closeTimeout, "3000");
        checkTooltip(shortTooltip, "This is a short tooltip");

        moveToRoot();

        // The tooltip should still be there despite being "cleared", as the
        // timeout hasn't expired yet.
        checkTooltip("This is a short tooltip");

        // assert that tooltip is present
        selectAndType(closeTimeout, "0");
        selectAndType(maxWidth, "100");

        testBenchElement(longTooltip).showTooltip();
        assertThat(getDriver().findElement(By.className("popupContent"))
                .getSize().getWidth(), is(100));
    }

    private void selectAndType(WebElement element, String value) {
        // select and replace text
        element.clear();
        // if null representation not set as "", need to move cursor to end and
        // remove text "null"
        element.sendKeys(value + Keys.ENTER);
    }
}