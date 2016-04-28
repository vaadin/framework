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
package com.vaadin.tests.components.splitpanel;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.HorizontalSplitPanelElement;
import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.testbench.elements.VerticalSplitPanelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for {@link SplitPositionChangeListeners}.
 * 
 * @author Vaadin Ltd
 */
public class SplitPanelWithMinimumAndMaximumTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    }

    @Test
    public void testMoveHorizontalSplitsToLimits() {
        // Amount of pixels to move each splitter (to left)
        int[] movements = { -250, -350, -320, -100, -200, -100, -170, -400 };

        // Expected final positions of splitters (at left limit)
        int[] finalPositions = { 60, 60, 100, 100, 478, 478, 550, 550 };

        List<HorizontalSplitPanelElement> splits = $(
                HorizontalSplitPanelElement.class).all();

        Actions actions = new Actions(driver);

        for (int i = 0; i < splits.size(); i++) {
            TestBenchElement splitter = splits.get(i).getSplitter();
            actions.clickAndHold(splitter).moveByOffset(movements[i], 0)
                    .release().perform();

            double newX = parseHorizontalPosition(splitter, i < 4);
            int expectedX = finalPositions[i];

            // Due to minor browser differences and sub-pixels we must allow 1px
            // of play between the expected and measured value
            assertTrue("When moving left, the splitter at index " + i
                    + " was at position " + newX + " (expected " + expectedX
                    + ").", Math.abs(newX - expectedX) <= 1);
        }

        // Amount of pixels to move each splitter (to right)
        movements = new int[] { 450, 450, 480, 480, 450, 450, 480, 480 };

        // Expected final positions of splitters (at right limit)
        finalPositions = new int[] { 478, 478, 550, 550, 60, 60, 100, 100 };

        for (int i = 0; i < splits.size(); i++) {
            TestBenchElement splitter = splits.get(i).getSplitter();
            actions.clickAndHold(splitter).moveByOffset(movements[i], 0)
                    .release().perform();

            double newX = parseHorizontalPosition(splitter, i < 4);
            int expectedX = finalPositions[i];

            assertTrue("When moving right, the splitter at index " + i
                    + " was at position " + newX + " (expected " + expectedX
                    + ").", Math.abs(newX - expectedX) <= 1);
        }
    }

    @Test
    public void testMoveVerticalSplitsToLimits() {
        $(TabSheetElement.class).first().openTab(1);

        // Amount of pixels to move each splitter (up)
        int[] movements = { -210, -360, -320, -70, -165, -20, -120, -260 };

        // Expected final positions of splitters (at upper limit)
        int[] finalPositions = { 52, 52, 100, 100, 413, 413, 400, 400 };

        List<VerticalSplitPanelElement> splits = $(
                VerticalSplitPanelElement.class).all();

        Actions actions = new Actions(driver);

        for (int i = 0; i < splits.size(); i++) {
            TestBenchElement splitter = splits.get(i).getSplitter();
            actions.clickAndHold(splitter).moveByOffset(0, movements[i])
                    .release().perform();

            double newY = parseVerticalPosition(splitter, i < 4);
            int expectedY = finalPositions[i];

            assertTrue("When moving up, the splitter at index " + i
                    + " was at position " + newY + " (expected " + expectedY
                    + ").", Math.abs(newY - expectedY) <= 1);
        }

        // Amount of pixels to move each splitter (down)
        movements = new int[] { 380, 380, 370, 370, 380, 380, 320, 320 };

        // Expected final positions of splitters (at lower limit)
        finalPositions = new int[] { 413, 413, 450, 450, 52, 52, 100, 100 };

        for (int i = 0; i < splits.size(); i++) {
            TestBenchElement splitter = splits.get(i).getSplitter();
            actions.clickAndHold(splitter).moveByOffset(0, movements[i])
                    .release().perform();

            double newY = parseVerticalPosition(splitter, i < 4);
            int expectedY = finalPositions[i];

            assertTrue("When moving down, the splitter at index " + i
                    + " was at position " + newY + " (expected " + expectedY
                    + ").", Math.abs(newY - expectedY) <= 1);
        }
    }

    @Test
    public void testHorizontalLimitsEnableDisable() {
        $(TabSheetElement.class).first().openTab(2);

        // Amount of pixels to move each splitter
        int[] movements = { -260, -70, 500, 130 };

        // Expected final positions of splitters (at either limit)
        int[] finalPositions = { 60, 0, 478, 591 };

        // Only one split panel in this test
        HorizontalSplitPanelElement split = $(HorizontalSplitPanelElement.class)
                .first();
        TestBenchElement splitter = split.getSplitter();

        Actions actions = new Actions(driver);

        // At left limit
        actions.clickAndHold(splitter).moveByOffset(movements[0], 0).release()
                .perform();
        double newX = parseHorizontalPosition(splitter, true);
        int expectedX = finalPositions[0];

        assertTrue("When moving to left limit, the splitter was at position "
                + newX + " (expected " + expectedX + ").",
                Math.abs(newX - expectedX) <= 1);

        // Disable left limit
        $(ButtonElement.class).get(0).click();

        // At absolute left
        actions.clickAndHold(splitter).moveByOffset(movements[1], 0).release()
                .perform();
        newX = parseHorizontalPosition(splitter, true);
        expectedX = finalPositions[1];

        assertTrue(
                "When moving to absolute left, the splitter was at position "
                        + newX + " (expected " + expectedX + ").",
                Math.abs(newX - expectedX) <= 1);

        // Enable left limit
        $(ButtonElement.class).get(1).click();

        newX = parseHorizontalPosition(splitter, true);
        expectedX = finalPositions[0];

        assertTrue(
                "When re-enabling the left limit, the splitter was at position "
                        + newX + " (expected " + expectedX + ").",
                Math.abs(newX - expectedX) <= 1);

        // At right limit
        actions.clickAndHold(splitter).moveByOffset(movements[2], 0).release()
                .perform();
        newX = parseHorizontalPosition(splitter, true);
        expectedX = finalPositions[2];

        assertTrue("When moving to right limit, the splitter was at position "
                + newX + " (expected " + expectedX + ").",
                Math.abs(newX - expectedX) <= 1);

        // Disable right limit
        $(ButtonElement.class).get(2).click();

        // At absolute right
        actions.clickAndHold(splitter).moveByOffset(movements[3], 0).release()
                .perform();
        newX = parseHorizontalPosition(splitter, true);
        expectedX = finalPositions[3];

        assertTrue(
                "When moving to absolute right, the splitter was at position "
                        + newX + " (expected " + expectedX + ").",
                Math.abs(newX - expectedX) <= 1);

        // Enable right limit
        $(ButtonElement.class).get(3).click();

        newX = parseHorizontalPosition(splitter, true);
        expectedX = finalPositions[2];

        assertTrue(
                "When re-enabling the right limit, the splitter was at position "
                        + newX + " (expected " + expectedX + ").",
                Math.abs(newX - expectedX) <= 1);
    }

    private double parseHorizontalPosition(TestBenchElement splitter,
            boolean left) {
        if (left) {
            return Double.parseDouble(splitter.getCssValue("left").replace(
                    "px", ""));
        } else {
            return Double.parseDouble(splitter.getCssValue("right").replace(
                    "px", ""));
        }
    }

    private double parseVerticalPosition(TestBenchElement splitter, boolean top) {
        if (top) {
            return Double.parseDouble(splitter.getCssValue("top").replace("px",
                    ""));
        } else {
            return Double.parseDouble(splitter.getCssValue("bottom").replace(
                    "px", ""));
        }
    }
}