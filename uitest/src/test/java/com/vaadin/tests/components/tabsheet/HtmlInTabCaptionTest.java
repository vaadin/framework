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

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.AccordionElement;
import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class HtmlInTabCaptionTest extends SingleBrowserTest {
    static final String PLAIN_TEXT_RED = "<font color='red'>red</font>";
    static final String HTML_TEXT_RED = "red";
    static final String PLAIN_TEXT_BLUE = "<font color='blue'>blue</font>";
    static final String HTML_TEXT_BLUE = "blue";

    @Test
    public void tabsheetWithoutHtmlCaptions() {
        openTestURL();
        TabSheetElement ts = $(TabSheetElement.class).get(0);
        Assert.assertEquals(PLAIN_TEXT_RED, getTab(ts, 0).getText());
        Assert.assertEquals(PLAIN_TEXT_BLUE, getTab(ts, 1).getText());
    }

    private WebElement getTab(TabSheetElement tabSheetElement, int i) {
        String className = "v-tabsheet-tabitem";
        if (tabSheetElement instanceof AccordionElement) {
            className = "v-accordion-item";
        }
        return tabSheetElement.findElements(By.className(className)).get(i);
    }

    @Test
    public void tabsheetWithHtmlCaptions() {
        openTestURL();
        TabSheetElement ts = $(TabSheetElement.class).get(1);
        Assert.assertEquals(HTML_TEXT_RED, getTab(ts, 0).getText());
        Assert.assertEquals(HTML_TEXT_BLUE, getTab(ts, 1).getText());
    }

    @Test
    public void accordionWithoutHtmlCaptions() {
        openTestURL();
        AccordionElement acc = $(AccordionElement.class).get(0);
        Assert.assertEquals(PLAIN_TEXT_RED, getTab(acc, 0).getText());
        Assert.assertEquals(PLAIN_TEXT_BLUE, getTab(acc, 1).getText());

    }

    @Test
    public void accordionWithHtmlCaptions() {
        openTestURL();
        AccordionElement acc = $(AccordionElement.class).get(1);
        Assert.assertEquals(HTML_TEXT_RED, getTab(acc, 0).getText());
        Assert.assertEquals(HTML_TEXT_BLUE, getTab(acc, 1).getText());
    }
}
