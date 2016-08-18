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
package com.vaadin.tests.components.grid;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.number.IsCloseTo.closeTo;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.OptionGroupElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that Grid gets correct height based on height mode, and resizes
 * properly with details row if height is undefined.
 *
 * @author Vaadin Ltd
 */
@TestCategory("grid")
public class GridHeightTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        waitForElementPresent(By.className("v-grid"));
    }

    @Test
    public void testGridHeightAndResizingUndefined()
            throws InterruptedException {
        assertNoErrors(testGridHeightAndResizing(GridHeight.UNDEFINED));
    }

    @Test
    public void testGridHeightAndResizingRow() throws InterruptedException {
        if (isIE8orIE9()) {
            /*
             * with IE8 and IE9 and this height mode grid resizes when it
             * shouldn't and doesn't resize when it should, pre-existing problem
             * that isn't within the scope of this ticket
             */
            return;
        }
        assertNoErrors(testGridHeightAndResizing(GridHeight.ROW3));
    }

    @Test
    public void testGridHeightAndResizingFull() throws InterruptedException {
        assertNoErrors(testGridHeightAndResizing(GridHeight.FULL));
    }

    private Map<AssertionError, Object[]> testGridHeightAndResizing(
            Object gridHeight) throws InterruptedException {
        Map<AssertionError, Object[]> errors = new HashMap<AssertionError, Object[]>();
        String caption;
        if (GridHeight.ROW3.equals(gridHeight)) {
            caption = gridHeight + " rows";
        } else {
            caption = (String) gridHeight;
        }
        $(OptionGroupElement.class).id("gridHeightSelector")
                .selectByText(caption);
        for (String gridWidth : GridHeight.gridWidths) {
            $(OptionGroupElement.class).id("gridWidthSelector")
                    .selectByText(gridWidth);
            for (String detailsRowHeight : GridHeight.detailsRowHeights) {
                $(OptionGroupElement.class).id("detailsHeightSelector")
                        .selectByText(detailsRowHeight);
                sleep(500);

                GridElement grid = $(LegacyGridElement.class).first();
                int initialHeight = grid.getSize().getHeight();
                try {
                    // check default height
                    assertGridHeight(getExpectedInitialHeight(gridHeight),
                            initialHeight);
                } catch (AssertionError e) {
                    errors.put(e, new Object[] { gridHeight, gridWidth,
                            detailsRowHeight, "initial" });
                }

                grid.getRow(2).click(5, 5);
                waitForElementPresent(By.id("lbl1"));

                int openHeight = grid.getSize().getHeight();
                try {
                    // check height with details row opened
                    assertGridHeight(getExpectedOpenedHeight(gridHeight,
                            detailsRowHeight), openHeight);
                } catch (AssertionError e) {
                    errors.put(e, new Object[] { gridHeight, gridWidth,
                            detailsRowHeight, "opened" });
                }

                grid.getRow(2).click(5, 5);
                waitForElementNotPresent(By.id("lbl1"));

                int afterHeight = grid.getSize().getHeight();
                try {
                    // check height with details row closed again
                    assertThat("Unexpected Grid Height", afterHeight,
                            is(initialHeight));
                } catch (AssertionError e) {
                    errors.put(e, new Object[] { gridHeight, gridWidth,
                            detailsRowHeight, "closed" });
                }
            }
        }
        return errors;
    }

    private void assertNoErrors(Map<AssertionError, Object[]> errors) {
        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder("Exceptions: ");
            for (Entry<AssertionError, Object[]> entry : errors.entrySet()) {
                sb.append("\n");
                for (Object value : entry.getValue()) {
                    sb.append(value);
                    sb.append(" - ");
                }
                sb.append(entry.getKey().getMessage());
            }
            Assert.fail(sb.toString());
        }
    }

    private int getExpectedInitialHeight(Object gridHeight) {
        int result = 0;
        if (GridHeight.UNDEFINED.equals(gridHeight)
                || GridHeight.ROW3.equals(gridHeight)) {
            result = 81;
        } else if (GridHeight.FULL.equals(gridHeight)) {
            // pre-existing issue
            result = 400;
        }
        return result;
    }

    private int getExpectedOpenedHeight(Object gridHeight,
            Object detailsRowHeight) {
        int result = 0;
        if (GridHeight.UNDEFINED.equals(gridHeight)) {
            if (GridHeight.PX100.equals(detailsRowHeight)) {
                result = 182;
            } else if (GridHeight.FULL.equals(detailsRowHeight)) {
                if (isIE8orIE9()) {
                    // pre-existing bug in IE8 & IE9, details row doesn't layout
                    // itself properly
                    result = 100;
                } else {
                    result = 131;
                }
            } else if (GridHeight.UNDEFINED.equals(detailsRowHeight)) {
                result = 100;
            }
        } else if (GridHeight.ROW3.equals(gridHeight)
                || GridHeight.FULL.equals(gridHeight)) {
            result = getExpectedInitialHeight(gridHeight);
        }
        return result;
    }

    private boolean isIE8orIE9() {
        DesiredCapabilities desiredCapabilities = getDesiredCapabilities();
        return BrowserUtil.isIE8(desiredCapabilities)
                || BrowserUtil.isIE(desiredCapabilities, 9);
    }

    private void assertGridHeight(int expected, int actual) {
        assertThat("Unexpected Grid Height", (double) actual,
                closeTo(expected, 1));
    }
}
