package com.vaadin.v7.tests.components.grid.basicfeatures.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;

public class GridClientKeyEventsTest extends GridBasicClientFeaturesTest {

    private List<String> eventOrder = Arrays.asList("Down", "Up", "Press");

    @Test
    public void testBodyKeyEvents() throws IOException {
        openTestURL();

        getGridElement().getCell(2, 2).click();

        new Actions(getDriver()).sendKeys("a").perform();

        for (int i = 0; i < 3; ++i) {
            assertEquals("Body key event handler was not called.",
                    "(2, 2) event: GridKey" + eventOrder.get(i) + "Event:["
                            + (eventOrder.get(i).equals("Press") ? "a" : 65)
                            + "]",
                    findElements(By.className("v-label")).get(i * 3).getText());

            assertTrue("Header key event handler got called unexpectedly.",
                    findElements(By.className("v-label")).get(i * 3 + 1)
                            .getText().isEmpty());
            assertTrue("Footer key event handler got called unexpectedly.",
                    findElements(By.className("v-label")).get(i * 3 + 2)
                            .getText().isEmpty());
        }

    }

    @Test
    public void testHeaderKeyEvents() throws IOException {
        openTestURL();

        getGridElement().getHeaderCell(0, 2).click();

        new Actions(getDriver()).sendKeys("a").perform();

        for (int i = 0; i < 3; ++i) {
            assertEquals("Header key event handler was not called.",
                    "(0, 2) event: GridKey" + eventOrder.get(i) + "Event:["
                            + (eventOrder.get(i).equals("Press") ? "a" : 65)
                            + "]",
                    findElements(By.className("v-label")).get(i * 3 + 1)
                            .getText());

            assertTrue("Body key event handler got called unexpectedly.",
                    findElements(By.className("v-label")).get(i * 3).getText()
                            .isEmpty());
            assertTrue("Footer key event handler got called unexpectedly.",
                    findElements(By.className("v-label")).get(i * 3 + 2)
                            .getText().isEmpty());
        }
    }

    @Test
    public void selectAllUsingKeyboard() {
        openTestURL();

        selectMenuPath("Component", "Header", "Prepend row");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "State", "Selection mode", "multi");

        // IE8 does not handle 1k rows well with assertions enabled. Less rows!
        selectMenuPath("Component", "DataSource",
                "Reset with 100 rows of Data");

        // Focus cell above select all checkbox
        getGridElement().getHeaderCell(0, 0).click();
        assertFalse(isRowSelected(1));
        new Actions(getDriver()).sendKeys(" ").perform();
        assertFalse(isRowSelected(1));

        // Move down to select all checkbox cell
        new Actions(getDriver()).sendKeys(Keys.ARROW_DOWN).perform();
        assertFalse(isRowSelected(1));
        new Actions(getDriver()).sendKeys(" ").perform(); // select all
        assertTrue(isRowSelected(1));
        new Actions(getDriver()).sendKeys(" ").perform(); // deselect all
        assertFalse(isRowSelected(1));

        // Move down to header below select all checkbox cell
        new Actions(getDriver()).sendKeys(Keys.ARROW_DOWN).perform();
        assertFalse(isRowSelected(1));
        new Actions(getDriver()).sendKeys(" ").perform(); // deselect all
        assertFalse(isRowSelected(1));

    }

    @Test
    public void testFooterKeyEvents() throws IOException {
        openTestURL();

        selectMenuPath("Component", "Footer", "Append row");
        getGridElement().getFooterCell(0, 2).click();

        new Actions(getDriver()).sendKeys("a").perform();

        for (int i = 0; i < 3; ++i) {
            assertEquals("Footer key event handler was not called.",
                    "(0, 2) event: GridKey" + eventOrder.get(i) + "Event:["
                            + (eventOrder.get(i).equals("Press") ? "a" : 65)
                            + "]",
                    findElements(By.className("v-label")).get(i * 3 + 2)
                            .getText());

            assertTrue("Body key event handler got called unexpectedly.",
                    findElements(By.className("v-label")).get(i * 3).getText()
                            .isEmpty());
            assertTrue("Header key event handler got called unexpectedly.",
                    findElements(By.className("v-label")).get(i * 3 + 1)
                            .getText().isEmpty());

        }
    }

    @Test
    public void testNoKeyEventsFromWidget() {
        openTestURL();

        selectMenuPath("Component", "Columns", "Column 2", "Header Type",
                "Widget Header");
        GridCellElement header = getGridElement().getHeaderCell(0, 2);
        header.findElement(By.tagName("button")).click();
        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();

        for (int i = 0; i < 3; ++i) {
            assertTrue("Header key event handler got called unexpectedly.",
                    findElements(By.className("v-label")).get(i * 3 + 1)
                            .getText().isEmpty());

        }
    }

}
