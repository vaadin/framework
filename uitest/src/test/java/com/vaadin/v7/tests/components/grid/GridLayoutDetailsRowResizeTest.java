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
package com.vaadin.v7.tests.components.grid;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.VerticalLayoutElement;
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

        VerticalLayoutElement layout = $(VerticalLayoutElement.class)
                .id("details");
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
        openTestURL();
        waitForElementPresent(By.className("v-grid"));

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
            assertEquals(
                    String.format("Unexpected button position: details row %s.",
                            i),
                    positions.get(button) + (i * labelHeight),
                    (double) button.getLocation().getY(), 1);
        }

        // toggle the contents
        for (ButtonElement button : buttons) {
            button.click();
        }

        // assert original positions back
        for (ButtonElement button : buttons) {
            assertEquals(String.format("Unexpected button position."),
                    positions.get(button), (double) button.getLocation().getY(),
                    1);
        }
    }

    private void assertLabelHeight(String id, double expectedHeight) {
        // 1px leeway for calculations
        assertEquals("Unexpected label height.", expectedHeight,
                (double) $(LabelElement.class).id(id).getSize().height, 1);
    }

    private void assertDetailsRowHeight(int layoutHeight) {
        // check that details row height matches layout height (1px leeway)
        WebElement detailsRow = findElement(By.className("v-grid-spacer"));
        assertEquals("Unexpected details row height",
                detailsRow.getSize().height, (double) layoutHeight, 1);
    }
}
