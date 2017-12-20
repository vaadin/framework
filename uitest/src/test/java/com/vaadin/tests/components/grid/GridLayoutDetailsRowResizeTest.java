/*
 * Copyright 2000-2017 Vaadin Ltd.
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that details row resizes along with the contents properly.
 *
 * @author Vaadin Ltd
 */
@TestCategory("grid")
public class GridLayoutDetailsRowResizeTest extends MultiBrowserTest {

    @Test
    public void testLabelHeights() {
        openTestURL();
        waitForElementPresent(By.className("v-grid"));

        GridElement grid = $(GridElement.class).first();

        grid.getCell(2, 0).click();
        waitForElementPresent(By.id("lbl2"));

        WebElement layout = grid.findElement(By.className("v-gridlayout"));
        int layoutHeight = layout.getSize().height;

        ButtonElement button = $(ButtonElement.class).id("btn");
        int buttonHeight = button.getSize().height;

        // height should be divided equally
        double expectedLabelHeight = (layoutHeight - buttonHeight) / 3;
        assertLabelHeight("lbl1", expectedLabelHeight);
        assertLabelHeight("lbl2", expectedLabelHeight);
        assertLabelHeight("lbl3", expectedLabelHeight);

        assertDetailsRowHeight(layoutHeight);

        // ensure fourth label isn't present yet
        assertElementNotPresent(By.id("lbl4"));

        button.click();
        waitForElementPresent(By.id("lbl4"));

        // get layout height after the new label has been added
        layoutHeight = layout.getSize().height;

        expectedLabelHeight = (layoutHeight - buttonHeight) / 4;
        assertLabelHeight("lbl1", expectedLabelHeight);
        assertLabelHeight("lbl2", expectedLabelHeight);
        assertLabelHeight("lbl3", expectedLabelHeight);
        assertLabelHeight("lbl4", expectedLabelHeight);

        assertDetailsRowHeight(layoutHeight);
    }

    @Test
    public void testMultipleDetailsRows() {
        if (Browser.PHANTOMJS.name()
                .equalsIgnoreCase(getDesiredCapabilities().getBrowserName())) {
            // For some inexplicable reason PhantomJS fails to click that
            // button, even if similar button clicks work just fine in other
            // tests. Didn't disable PhantomJS altogether so that the other test
            // at least could work in the initial pre-merge regression check.
            return;
        }

        setDebug(true);
        openTestURL();
        waitForElementPresent(By.className("v-grid"));

        // all rows won't fit if using Valo, toggle back to reindeer
        ButtonElement themeButton = $(ButtonElement.class)
                .caption("Toggle theme").first();
        int buttonHeight = themeButton.getSize().height;

        themeButton.click();

        // wait for the theme change to take hold
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver arg0) {
                return buttonHeight > $(ButtonElement.class)
                        .caption("Toggle theme").first().getSize().height;
            }

            @Override
            public String toString() {
                // Expected condition failed: waiting for ...
                return "button's theme to change";
            }
        });

        ButtonElement detailsButton = $(ButtonElement.class)
                .caption("Open details").first();

        detailsButton.click();
        waitForElementPresent(By.id("lbl2"));

        List<ButtonElement> buttons = $(ButtonElement.class)
                .caption("Toggle visibility").all();
        assertEquals("Unexpected amount of details rows.", 3, buttons.size());

        Map<ButtonElement, Integer> positions = new LinkedHashMap<ButtonElement, Integer>();
        Map<Integer, ButtonElement> ordered = new TreeMap<Integer, ButtonElement>();
        for (ButtonElement button : buttons) {
            positions.put(button, button.getLocation().getY());
            ordered.put(button.getLocation().getY(), button);
        }
        int labelHeight = 0;
        for (LabelElement label : $(LabelElement.class).all()) {
            if ("test1".equals(label.getText())) {
                labelHeight = label.getSize().height;
            }
        }

        // toggle the contents
        for (ButtonElement button : buttons) {
            button.click();
        }

        int i = 0;
        for (Entry<Integer, ButtonElement> entry : ordered.entrySet()) {
            ++i;
            ButtonElement button = entry.getValue();
            assertThat(
                    String.format("Unexpected button position: details row %s.",
                            i),
                    (double) button.getLocation().getY(),
                    closeTo(positions.get(button) + (i * labelHeight), 1d));
        }

        // toggle the contents
        for (ButtonElement button : buttons) {
            button.click();
        }

        // assert original positions back
        for (ButtonElement button : buttons) {
            assertThat(String.format("Unexpected button position."),
                    (double) button.getLocation().getY(),
                    closeTo(positions.get(button), 1d));
        }
    }

    private void assertLabelHeight(String id, double expectedHeight) {
        // 1px leeway for calculations
        assertThat("Unexpected label height.",
                (double) $(LabelElement.class).id(id).getSize().height,
                closeTo(expectedHeight, 1d));
    }

    private void assertDetailsRowHeight(int layoutHeight) {
        // check that details row height matches layout height (3px leeway)
        WebElement detailsRow = findElement(By.className("v-grid-spacer"));
        assertThat("Unexpected details row height", (double) layoutHeight,
                closeTo(detailsRow.getSize().height, 3d));
    }
}
