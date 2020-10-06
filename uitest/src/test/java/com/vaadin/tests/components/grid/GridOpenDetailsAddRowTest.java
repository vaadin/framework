package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridOpenDetailsAddRowTest extends MultiBrowserTest {

    @Test
    public void addRow() {
        openTestURL();
        waitUntilLoadingIndicatorNotVisible();

        // confirm initial state
        List<WebElement> spacers = findElements(By.className("v-grid-spacer"));
        assertEquals("Unexpected initial amount of spacers", 3, spacers.size());

        // add a row
        $(ButtonElement.class).first().click();
        waitUntilLoadingIndicatorNotVisible();

        // ensure all existing spacers are still visible, as well as the new one
        spacers = findElements(By.className("v-grid-spacer"));
        assertEquals("Unexpected amount of spacers after adding a row", 4,
                spacers.size());

        assertEquals("Unexpected spacer contents for new row", "details - row4",
                spacers.get(3).getText());

        assertEquals("Unexpected spacer contents for first row",
                "details - row1", spacers.get(0).getText());
    }
}
