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
        List<WebElement> cells = driver.findElements(
                By.className("v-inline-datefield-calendarpanel-day"));

        // Verify the range is rendered correctly.
        assertCell(cells.get(0), "30", true);
        assertCell(cells.get(1), "1", false);
        assertCell(cells.get(2), "2", false);
        assertCell(cells.get(3), "3", true);
    }

    private void assertCell(WebElement cell, String text,
            boolean outsideRange) {
        assertEquals(text, cell.getText());
        assertEquals(outsideRange,
                cell.getAttribute("class").contains("outside-range"));
    }

}
