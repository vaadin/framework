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
package com.vaadin.tests.components.formlayout;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.FormLayoutElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserThemeTest;

public class FormLayoutResizingTest extends MultiBrowserThemeTest {
    @Test
    public void testTableResizing() {
        openTestURL();

        List<TableElement> tables = $(TableElement.class).all();
        Assert.assertEquals("Sanity check", 2, tables.size());

        List<FormLayoutElement> layouts = $(FormLayoutElement.class).all();
        Assert.assertEquals("Sanity check", 2, layouts.size());

        ButtonElement toggleButton = $(ButtonElement.class).first();

        int[] originalWidths = getWidths(tables);

        // In some browser and theme combinations, the original rendering is
        // slightly too wide. Find out this overshoot and adjust the expected
        // table width accordingly.
        for (int i = 0; i < 2; i++) {
            FormLayoutElement formLayout = layouts.get(i);
            WebElement table = formLayout.findElement(By.tagName("table"));
            int overshoot = table.getSize().width - formLayout.getSize().width;
            originalWidths[i] -= overshoot;
        }

        // Toggle size from 400 px to 600 px
        toggleButton.click();

        int[] expandedWidths = getWidths(tables);

        Assert.assertEquals("Table should have grown ", originalWidths[0] + 200,
                expandedWidths[0]);
        Assert.assertEquals("Wrapped table should have grown ",
                originalWidths[1] + 200, expandedWidths[1]);

        // Toggle size from 600 px to 400 px
        toggleButton.click();

        int[] collapsedWidths = getWidths(tables);

        Assert.assertEquals("Table should return to original width ",
                originalWidths[0], collapsedWidths[0]);
        Assert.assertEquals("Wrapped table should return to original width ",
                originalWidths[1], collapsedWidths[1]);

        // Verify that growing is not restricted after triggering the fix
        // Toggle size from 400 px to 600 px
        toggleButton.click();

        expandedWidths = getWidths(tables);

        Assert.assertEquals("Table should have grown ", originalWidths[0] + 200,
                expandedWidths[0]);
        Assert.assertEquals("Wrapped table should have grown ",
                originalWidths[1] + 200, expandedWidths[1]);
    }

    @Override
    protected boolean useNativeEventsForIE() {
        return !BrowserUtil.isIE(getDesiredCapabilities(), 11);
    }

    private static int[] getWidths(List<TableElement> tables) {
        int[] widths = new int[tables.size()];
        for (int i = 0; i < widths.length; i++) {
            widths[i] = tables.get(i).getSize().width;
        }
        return widths;
    }
}
