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
package com.vaadin.tests.components.grid.basicfeatures.escalator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.components.grid.basicfeatures.EscalatorBasicClientFeaturesTest;

public class EscalatorColspanTest extends EscalatorBasicClientFeaturesTest {
    private static final int NO_COLSPAN = 1;

    @Test
    public void testNoColspan() {
        openTestURL();
        populate();

        assertEquals(NO_COLSPAN, getColSpan(getHeaderCell(0, 0)));
        assertEquals(NO_COLSPAN, getColSpan(getBodyCell(0, 0)));
        assertEquals(NO_COLSPAN, getColSpan(getFooterCell(0, 0)));
    }

    @Test
    public void testColspan() {
        openTestURL();
        populate();

        int firstCellWidth = getWidth(getBodyCell(0, 0));
        int secondCellWidth = getWidth(getBodyCell(0, 1));
        int doubleCellWidth = firstCellWidth + secondCellWidth;

        selectMenuPath(FEATURES, COLUMN_SPANNING, COLSPAN_NORMAL);

        WebElement bodyCell = getBodyCell(0, 0);
        assertEquals(2, getColSpan(bodyCell));
        assertEquals(doubleCellWidth, getWidth(bodyCell));
    }

    @Test
    public void testColspanToggle() {
        openTestURL();
        populate();

        int singleCellWidth = getWidth(getBodyCell(0, 0));

        selectMenuPath(FEATURES, COLUMN_SPANNING, COLSPAN_NORMAL);
        selectMenuPath(FEATURES, COLUMN_SPANNING, COLSPAN_NONE);

        WebElement bodyCell = getBodyCell(0, 0);
        assertEquals(NO_COLSPAN, getColSpan(bodyCell));
        assertEquals(singleCellWidth, getWidth(bodyCell));
    }

    private static int getWidth(WebElement element) {
        String widthString = element.getCssValue("width"); // e.g. 100px
        if ("0".equals(widthString)) {
            return 0;
        } else if (widthString.endsWith("px")) {
            return Integer.parseInt(widthString.substring(0,
                    widthString.length() - 2));
        } else {
            throw new IllegalStateException("Element width expressed "
                    + "in an unsupported format: " + widthString);
        }
    }

    private static int getColSpan(WebElement cell) {
        String attribute = cell.getAttribute("colspan");
        if (attribute == null) {
            return NO_COLSPAN;
        } else {
            return Integer.parseInt(attribute);
        }
    }
}
