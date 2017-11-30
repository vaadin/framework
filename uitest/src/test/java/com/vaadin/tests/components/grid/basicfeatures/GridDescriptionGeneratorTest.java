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
package com.vaadin.tests.components.grid.basicfeatures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;

public class GridDescriptionGeneratorTest extends GridBasicFeaturesTest {

    @Test
    public void testCellDescription() {
        openTestURL();
        selectCellGenerator("Default");

        showCellTooltip(1, 0);
        String tooltipText = getTooltipText();
        assertEquals("Tooltip text", "Cell tooltip for row 1, column 0",
                tooltipText);

        showCellTooltip(1, 1);
        assertTrue("Tooltip should not be present in cell (1, 1) ",
                getTooltipText().isEmpty());
    }

    @Test
    public void testRowDescription() {
        openTestURL();
        selectRowGenerator("Default");

        showCellTooltip(5, 3);
        String tooltipText = getTooltipText();
        assertEquals("Tooltip text", "Row tooltip for row 5", tooltipText);

        showCellTooltip(15, 3);
        tooltipText = getTooltipText();
        assertEquals("Tooltip text", "Row tooltip for row 15", tooltipText);
    }

    @Test
    public void testRowAndCellDescription() {
        openTestURL();
        selectRowGenerator("Default");
        selectCellGenerator("Default");

        showCellTooltip(5, 0);
        String tooltipText = getTooltipText();
        assertEquals("Tooltip text", "Cell tooltip for row 5, column 0",
                tooltipText);

        showCellTooltip(5, 3);
        tooltipText = getTooltipText();
        assertEquals("Tooltip text", "Row tooltip for row 5", tooltipText);
    }

    @Test
    public void testContentTypes() {
        openTestURL();

        selectCellGenerator("Default");
        showCellTooltip(1, 0);
        /*
         * When porting this to the v7 version in Framework 8, the default
         * should be changed to PREFORMATTED to preserve the more secure default
         * that has accidentally been used there.
         */
        assertHtmlTooltipShown();

        selectRowGenerator("Default");
        showCellTooltip(1, 1);
        /*
         * When porting this to the v7 version in Framework 8, the default
         * should be changed to PREFORMATTED to preserve the more secure default
         * that has accidentally been used there.
         */
        assertHtmlTooltipShown();

        selectCellGenerator("Plain text");
        showCellTooltip(2, 0);
        assertPlainTooltipShown();

        selectRowGenerator("Plain text");
        showCellTooltip(2, 1);
        assertPlainTooltipShown();

        selectCellGenerator("Preformatted");
        showCellTooltip(3, 0);
        assertPreTooltipShown();

        selectRowGenerator("Preformatted");
        showCellTooltip(3, 1);
        assertPreTooltipShown();

        selectCellGenerator("HTML");
        showCellTooltip(4, 0);
        assertHtmlTooltipShown();

        selectRowGenerator("HTML");
        showCellTooltip(4, 1);
        assertHtmlTooltipShown();
    }

    private void assertPreTooltipShown() {
        assertTrue("Tooltip should contain <b> as text", getTooltipText()
                .contains("<b>"));
        assertTrue("Tooltip should contain a newline", getTooltipText()
                .contains("\n"));
    }

    private void assertPlainTooltipShown() {
        assertTrue("Tooltip should contain <b> as text", getTooltipText()
                .contains("<b>"));
        assertFalse("Tooltip should not contain a newline", getTooltipText()
                .contains("\n"));
    }

    private void assertHtmlTooltipShown() {
        assertTrue("Tooltip should contain <b> tag",
                isElementPresent(By.cssSelector(".v-tooltip-text b")));
    }

    private void showCellTooltip(int row, int col) {
        getGridElement().getCell(row, col).showTooltip();
    }

    private void selectCellGenerator(String name) {
        selectMenuPath("Component", "State", "Cell description generator", name);
    }

    private void selectRowGenerator(String name) {
        selectMenuPath("Component", "State", "Row description generator", name);
    }

    private String getTooltipText() {
        return findElement(By.className("v-tooltip-text")).getText();
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersExcludingFirefox();
    }

}
