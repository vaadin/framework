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
package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateRangeWithSqlDateTest extends MultiBrowserTest {

    @Test
    public void testDateRange() {
        openTestURL();

        // Get all cells of the inline datefield.
        List<WebElement> cells = driver.findElements(By
                .className("v-inline-datefield-calendarpanel-day"));

        // Verify the range is rendered correctly.
        assertCell(cells.get(0), "30", true);
        assertCell(cells.get(1), "1", false);
        assertCell(cells.get(2), "2", false);
        assertCell(cells.get(3), "3", true);
    }

    private void assertCell(WebElement cell, String text, boolean outsideRange) {
        assertEquals(text, cell.getText());
        assertEquals(outsideRange,
                cell.getAttribute("class").contains("outside-range"));
    }

}
