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
package com.vaadin.tests.components.tabsheet;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class TabSheetErrorTooltipTest extends MultiBrowserTest {

    @Test
    public void checkTooltips() throws IOException {
        openTestURL();

        testBenchElement(getTab(0)).showTooltip();
        assertNoTooltip();

        testBenchElement(getTab(1)).showTooltip();
        assertErrorMessage("Error!");
        assertTooltip("");

        testBenchElement(getTab(2)).showTooltip();
        assertErrorMessage("");
        assertTooltip("This is a tab");

        testBenchElement(getTab(3)).showTooltip();
        assertErrorMessage("Error!");
        assertTooltip("This tab has both an error and a description");
    }

    private WebElement getTab(int index) {
        return vaadinElement("/VTabsheet[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild["
                + index + "]/domChild[0]");
    }

    private WebElement getTooltip() {
        return getDriver().findElement(
                By.xpath("//div[@class='v-tooltip-text']"));
    }

    private WebElement getErrorMessage() {
        return getDriver().findElement(
                By.xpath("//div[@class='v-errormessage']"));
    }

    private void assertTooltip(String tooltip) {
        Assert.assertEquals(tooltip, getTooltip().getText());
    }

    private void assertErrorMessage(String message) {
        Assert.assertEquals(message, getErrorMessage().getText());
    }

    private void assertNoTooltip() {
        try {
            getTooltip();
        } catch (NoSuchElementException e) {
            return;
        }
        Assert.fail("Tooltip exists");
    }
}
