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
package com.vaadin.tests.components.gridlayout;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.CssLayoutElement;
import com.vaadin.testbench.elements.GridLayoutElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridLayoutExtraSpacingTest extends MultiBrowserTest {

    @Test
    public void componentRowFour() throws IOException, Exception {
        openTestURL();
        CssLayoutElement component = $(CssLayoutElement.class).first();
        GridLayoutElement gridLayout = $(GridLayoutElement.class).first();

        // Spacing on, not hiding empty rows/columns
        // There should be 3 * 6px spacing (red) above the csslayout
        verifySpacingAbove(3 * 6, gridLayout, component);

        CheckBoxElement spacingCheckbox = $(CheckBoxElement.class).caption(
                "spacing").first();
        check(spacingCheckbox);

        // Spacing off, not hiding empty rows/columns
        // There should not be any spacing (red) above the csslayout
        verifySpacingAbove(0, gridLayout, component);

        CheckBoxElement hideRowsColumnsCheckbox = $(CheckBoxElement.class)
                .caption("hide empty rows/columns").first();
        check(hideRowsColumnsCheckbox);

        // Spacing off, hiding empty rows/columns
        // There should not be any spacing (red) above the csslayout
        verifySpacingAbove(0, gridLayout, component);

        check(spacingCheckbox);
        // Spacing on, hiding empty rows/columns
        // There should not be any spacing (red) above or below the csslayout

        // Oh PhantomJs...
        sleep(100);
        // FIXME: This should be 0 but there is a bug somewhere
        // verifySpacingAbove(0, gridLayout, component);
        verifySpacingBelow(6, gridLayout, component);

    }

    /**
     * workaround for http://dev.vaadin.com/ticket/13763
     */
    private void check(CheckBoxElement checkbox) {
        WebElement cb = checkbox.findElement(By.xpath("input"));
        if (BrowserUtil.isChrome(getDesiredCapabilities())) {
            testBenchElement(cb).click(0, 0);
        } else {
            cb.click();
        }
    }

    private void verifySpacingAbove(int spacing, GridLayoutElement gridLayout,
            CssLayoutElement component) {
        assertHeight(component, 500 - spacing, 1);
        int offset = component.getLocation().getY()
                - gridLayout.getLocation().getY();
        Assert.assertEquals(spacing, offset);

    }

    private void verifySpacingBelow(int spacing, GridLayoutElement gridLayout,
            CssLayoutElement component) {
        assertHeight(component, 500 - spacing, 1);

        int offset = component.getLocation().getY()
                - gridLayout.getLocation().getY();
        Assert.assertEquals(0, offset);

    }

    private void assertHeight(WebElement component, int height, int tolerance) {
        Assert.assertTrue(Math.abs(height - component.getSize().getHeight()) <= tolerance);
    }
}
