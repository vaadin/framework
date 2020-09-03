package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridSelectAllStatusTest extends MultiBrowserTest {

    @Test
    public void checkSelectAllStatus() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        LabelElement selectAllStatusLabel = $(LabelElement.class).id("status");
        WebElement selectAllCheckbox = grid
                .findElement(By.className("v-grid-select-all-checkbox"));

        // select all
        selectAllCheckbox.click();
        assertTrue(
                "The status of the select-all checkbox is expected to be True.",
                Boolean.parseBoolean(selectAllStatusLabel.getText()));

        // De-select all selections
        selectAllCheckbox.click();
        assertFalse(
                "The status of the select-all checkbox is expected to be False.",
                Boolean.parseBoolean(selectAllStatusLabel.getText()));
    }
}
