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
package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@SuppressWarnings("boxing")
@TestCategory("grid")
public abstract class AbstractGridColumnAutoWidthTest extends MultiBrowserTest {

    public static final int TOTAL_MARGIN_PX = 13;

    @Before
    public void before() {
        openTestURL();
    }

    @Test
    public void testNarrowHeaderWideBody() {
        WebElement[] col = getColumn(1);
        int headerWidth = col[0].getSize().getWidth();
        int bodyWidth = col[1].getSize().getWidth();
        int colWidth = col[2].getSize().getWidth() - TOTAL_MARGIN_PX;

        assertLessThan("header should've been narrower than body", headerWidth,
                bodyWidth);
        assertEquals("column should've been roughly as wide as the body",
                bodyWidth, colWidth, 5);
    }

    @Test
    public void testWideHeaderNarrowBody() {
        WebElement[] col = getColumn(2);
        int headerWidth = col[0].getSize().getWidth();
        int bodyWidth = col[1].getSize().getWidth();
        int colWidth = col[2].getSize().getWidth() - TOTAL_MARGIN_PX;

        assertGreater("header should've been wider than body", headerWidth,
                bodyWidth);
        assertEquals("column should've been roughly as wide as the header",
                headerWidth, colWidth, 5);

    }

    @Test
    public void testTooNarrowColumn() {
        if (BrowserUtil.isIE(getDesiredCapabilities())) {
            // IE can't deal with overflow nicely.
            return;
        }

        WebElement[] col = getColumn(3);
        int headerWidth = col[0].getSize().getWidth();
        int colWidth = col[2].getSize().getWidth() - TOTAL_MARGIN_PX;

        assertLessThan("column should've been narrower than content", colWidth,
                headerWidth);
    }

    @Test
    public void testTooWideColumn() {
        WebElement[] col = getColumn(4);
        int headerWidth = col[0].getSize().getWidth();
        int colWidth = col[2].getSize().getWidth() - TOTAL_MARGIN_PX;

        assertGreater("column should've been wider than content", colWidth,
                headerWidth);
    }

    @Test
    public void testColumnsRenderCorrectly() throws IOException {
        compareScreen("initialRender");
    }

    private WebElement[] getColumn(int i) {
        WebElement[] col = new WebElement[3];
        col[0] = getDriver().findElement(
                By.xpath("//thead//th[" + (i + 1) + "]/span"));
        col[1] = getDriver().findElement(
                By.xpath("//tbody//td[" + (i + 1) + "]/span"));
        col[2] = getDriver().findElement(
                By.xpath("//tbody//td[" + (i + 1) + "]"));
        return col;
    }

}
