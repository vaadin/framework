package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridRendererChangeTest extends MultiBrowserTest {

    @Test
    public void testChangeRenderer() {
        setDebug(true);
        openTestURL();

        GridCellElement cell = $(GridElement.class).first().getCell(0, 0);
        assertTrue("No button in the first cell.",
                cell.isElementPresent(By.tagName("button")));
        int width = cell.getSize().getWidth();

        List<ButtonElement> buttons = $(ButtonElement.class).all();
        Collections.reverse(buttons);

        // Order: TextRenderer, HTMLRenderer, ButtonRenderer
        for (ButtonElement button : buttons) {
            button.click();
            assertNoErrorNotifications();
            cell = $(GridElement.class).first().getCell(0, 0);
            assertEquals("Cell size changed", width, cell.getSize().getWidth());
        }

        assertTrue("No button in the first cell.",
                cell.isElementPresent(By.tagName("button")));
    }

}