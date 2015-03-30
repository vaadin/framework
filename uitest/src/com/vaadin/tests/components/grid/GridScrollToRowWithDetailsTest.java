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

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import com.vaadin.shared.ui.grid.Range;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.components.grid.basicfeatures.element.CustomGridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridScrollToRowWithDetailsTest extends MultiBrowserTest {

    private static class Param {
        private final int rowIndex;
        private final boolean useGenerator;
        private final boolean scrollFirstToBottom;
        private final int scrollTarget;

        public Param(int rowIndex, boolean useGenerator,
                boolean scrollFirstToBottom, int scrollTarget) {
            this.rowIndex = rowIndex;
            this.useGenerator = useGenerator;
            this.scrollFirstToBottom = scrollFirstToBottom;
            this.scrollTarget = Math.max(0, scrollTarget);
        }

        public int getRowIndex() {
            return rowIndex;
        }

        public boolean useGenerator() {
            return useGenerator;
        }

        public boolean scrollFirstToBottom() {
            return scrollFirstToBottom;
        }

        public int getScrollTarget() {
            return scrollTarget;
        }

        @Override
        public String toString() {
            return "Param [rowIndex=" + getRowIndex() + ", useGenerator="
                    + useGenerator() + ", scrollFirstToBottom="
                    + scrollFirstToBottom() + ", scrollTarget="
                    + getScrollTarget() + "]";
        }
    }

    public static Collection<Param> parameters() {
        List<Param> data = new ArrayList<Param>();

        int[][] params = new int[][] {// @formatter:off
            // row, top+noGen, top+gen, bot+noGen, bot+gen
            { 0, 0, 0, 0, 0 }, 
            { 500, 18741, 18723, 19000, 19000 },
            { 999, 37703, 37685, 37703, 37685 },
        };
        // @formatter:on

        for (int i[] : params) {
            int rowIndex = i[0];
            int targetTopScrollWithoutGenerator = i[1];
            int targetTopScrollWithGenerator = i[2];
            int targetBottomScrollWithoutGenerator = i[3];
            int targetBottomScrollWithGenerator = i[4];

            data.add(new Param(rowIndex, false, false,
                    targetTopScrollWithoutGenerator));
            data.add(new Param(rowIndex, true, false,
                    targetTopScrollWithGenerator));
            data.add(new Param(rowIndex, false, true,
                    targetBottomScrollWithoutGenerator));
            data.add(new Param(rowIndex, true, true,
                    targetBottomScrollWithGenerator));
        }

        return data;
    }

    @Before
    public void setUp() {
        setDebug(true);
    }

    @Test
    public void toggleAndScroll() throws Throwable {
        for (Param param : parameters()) {
            try {
                openTestURL();
                useGenerator(param.useGenerator());
                scrollToBottom(param.scrollFirstToBottom());

                // the tested method
                toggleAndScroll(param.getRowIndex());

                Range allowedRange = Range.withLength(
                        param.getScrollTarget() - 5, 10);
                assertTrue(
                        allowedRange + " does not contain " + getScrollTop(),
                        allowedRange.contains(getScrollTop()));
            } catch (Throwable t) {
                throw new Throwable("" + param, t);
            }
        }
    }

    @Test
    public void scrollAndToggle() throws Throwable {
        for (Param param : parameters()) {
            try {
                openTestURL();
                useGenerator(param.useGenerator());
                scrollToBottom(param.scrollFirstToBottom());

                // the tested method
                scrollAndToggle(param.getRowIndex());

                Range allowedRange = Range.withLength(
                        param.getScrollTarget() - 5, 10);
                assertTrue(
                        allowedRange + " does not contain " + getScrollTop(),
                        allowedRange.contains(getScrollTop()));
            } catch (Throwable t) {
                throw new Throwable("" + param, t);
            }
        }
    }

    private void scrollToBottom(boolean scrollFirstToBottom) {
        if (scrollFirstToBottom) {
            executeScript("arguments[0].scrollTop = 9999999",
                    getVerticalScrollbar());
        }
    }

    private void useGenerator(boolean use) {
        CheckBoxElement checkBox = $(CheckBoxElement.class).first();
        boolean isChecked = isCheckedValo(checkBox);
        if (use != isChecked) {
            clickValo(checkBox);
        }
    }

    @SuppressWarnings("boxing")
    private boolean isCheckedValo(CheckBoxElement checkBoxElement) {
        WebElement checkbox = checkBoxElement.findElement(By.tagName("input"));
        Object value = executeScript("return arguments[0].checked;", checkbox);
        return (Boolean) value;
    }

    private void clickValo(CheckBoxElement checkBoxElement) {
        checkBoxElement.findElement(By.tagName("label")).click();
    }

    private Object executeScript(String string, Object... param) {
        return ((JavascriptExecutor) getDriver()).executeScript(string, param);
    }

    private void scrollAndToggle(int row) {
        setRow(row);
        getScrollAndToggle().click();
    }

    private void toggleAndScroll(int row) {
        setRow(row);
        getToggleAndScroll().click();
    }

    private ButtonElement getScrollAndToggle() {
        return $(ButtonElement.class).caption("Scroll and toggle").first();
    }

    private ButtonElement getToggleAndScroll() {
        return $(ButtonElement.class).caption("Toggle and scroll").first();
    }

    private void setRow(int row) {
        $(TextFieldElement.class).first().setValue(String.valueOf(row));
    }

    private CustomGridElement getGrid() {
        return $(CustomGridElement.class).first();
    }

    private int getScrollTop() {
        return ((Long) executeScript("return arguments[0].scrollTop;",
                getVerticalScrollbar())).intValue();
    }

    private WebElement getVerticalScrollbar() {
        WebElement scrollBar = getGrid().findElement(
                By.className("v-grid-scroller-vertical"));
        return scrollBar;
    }
}
