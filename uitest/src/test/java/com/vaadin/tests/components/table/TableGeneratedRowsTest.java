package com.vaadin.tests.components.table;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

@SuppressWarnings("deprecation")
public class TableGeneratedRowsTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return Tables.class;
    }

    @Test
    public void changeRowGenerator() {
        openTestURL();

        TableElement table = $(TableElement.class).first();

        /* No row generator */

        TestBenchElement cell = table.getCell(4, 0);
        int tableWidth = table.getSize().getWidth();
        int cellWidth = cell.getSize().getWidth();
        int buffer = 40; // for paddings, borders, scrollbars, and bit extra
        int bufferedTableWidth = tableWidth - buffer;
        String text = cell.getText();

        assertFalse("Unexpected cell contents without applying row generator: "
                + text, text != null && text.startsWith("FOOBARBAZ"));
        assertLessThan(
                "Cell shouldn't be spanned without applying row generator: "
                        + cellWidth + " isn't less than " + (tableWidth / 10),
                cellWidth, tableWidth / 10);

        /* Spanned row generator */

        selectMenuPath("Component", "Features", "Row generator",
                "Every fifth row, spanned");

        cell = table.getCell(4, 0);
        cellWidth = cell.getSize().getWidth();
        text = cell.getText();

        assertTrue(
                "Unexpected cell contents for spanned generated row: " + text,
                text != null && text.startsWith("FOOBARBAZ"));
        assertLessThanOrEqual(
                "Spanned cell isn't wide enough for spanned generated row: "
                        + cellWidth + " isn't more than " + bufferedTableWidth,
                bufferedTableWidth, cellWidth);

        /* Non-spanned row generator */

        selectMenuPath("Component", "Features", "Row generator",
                "Every tenth row, no spanning");

        cell = table.getCell(4, 0);
        cellWidth = cell.getSize().getWidth();
        text = cell.getText();

        assertFalse(
                "Unexpected cell contents after replacing spanned row generator: "
                        + text,
                text != null && text.startsWith("FOOBARBAZ"));
        assertLessThan(
                "Cell shouldn't be spanned anymore after replacing spanned row generator: "
                        + cellWidth + " isn't less than " + (tableWidth / 10),
                cellWidth, tableWidth / 10);

        cell = table.getCell(9, 0);
        cellWidth = cell.getSize().getWidth();
        text = cell.getText();

        assertTrue("Unexpected cell contents for non-spanned generated row: "
                + text, text != null && text.startsWith("FOO0"));
        assertLessThan(
                "Unexpected cell width for non-spanned generated row: "
                        + cellWidth + " isn't less than " + (tableWidth / 10),
                cellWidth, tableWidth / 10);

        /* Spanned and html formatted row generator */

        selectMenuPath("Component", "Features", "Row generator",
                "Every eight row, spanned, html formatted");

        cell = table.getCell(9, 0);
        cellWidth = cell.getSize().getWidth();
        text = cell.getText();

        assertFalse(
                "Unexpected cell contents after replacing non-spanned row generator: "
                        + text,
                text != null && text.startsWith("FOO0"));
        assertLessThan(
                "Unexpected cell width after replacing non-spanned row generator: "
                        + cellWidth + " isn't less than " + (tableWidth / 10),
                cellWidth, tableWidth / 10);

        cell = table.getCell(7, 0);
        cellWidth = cell.getSize().getWidth();
        text = cell.getText();

        assertTrue("Unexpected contents for spanned and html formatted cell: "
                + text, text != null && text.startsWith("FOO BAR BAZ"));
        assertLessThanOrEqual(
                "Spanned and html formatted cell isn't wide enough: "
                        + cellWidth + " isn't more than " + bufferedTableWidth,
                bufferedTableWidth, cellWidth);

        WebElement bazSpan = cell.findElement(By.tagName("span"));
        if (bazSpan == null) {
            fail("Unexpected styling: no span found");
        } else {
            String bazStyle = bazSpan.getAttribute("style");
            assertTrue("Unexpected styling: " + bazStyle,
                    bazStyle != null && bazStyle.contains("color: red"));
        }

        /* Spanned row generator for all rows */

        selectMenuPath("Component", "Features", "Row generator",
                "Every row, spanned");

        for (int i = 0; i < 10; ++i) {
            cell = table.getCell(i, 0);
            cellWidth = cell.getSize().getWidth();
            text = cell.getText();

            assertTrue(
                    "Unexpected cell contents with every row spanned: " + text,
                    text != null && text.startsWith("SPANNED"));
            assertLessThanOrEqual(
                    "Spanned cell isn't wide enough with every row spanned: "
                            + cellWidth + " isn't more than "
                            + bufferedTableWidth,
                    bufferedTableWidth, cellWidth);
        }

        /* No row generator */

        selectMenuPath("Component", "Features", "Row generator", "None");

        for (int i = 0; i < 10; ++i) {
            cell = table.getCell(i, 0);
            cellWidth = cell.getSize().getWidth();
            text = cell.getText();

            assertFalse(
                    "Unexpected cell contents without row generator: " + text,
                    text != null && text.startsWith("SPANNED"));
            assertLessThan(
                    "Unexpected cell width without row generator: " + cellWidth
                            + " isn't less than " + (tableWidth / 10),
                    cellWidth, tableWidth / 10);
        }
    }
}
