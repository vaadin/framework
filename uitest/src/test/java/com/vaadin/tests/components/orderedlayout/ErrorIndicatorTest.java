package com.vaadin.tests.components.orderedlayout;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ErrorIndicatorTest extends MultiBrowserTest {

    @Test
    public void verifyTooltips() {
        String tooltipText;
        openTestURL();

        $(TextFieldElement.class).first().showTooltip();
        tooltipText = driver.findElement(By.className("v-tooltip")).getText();
        assertEquals(tooltipText, "Vertical layout tooltip");

        $(TextFieldElement.class).get(1).showTooltip();
        tooltipText = driver.findElement(By.className("v-tooltip")).getText();
        assertEquals(tooltipText, "Horizontal layout tooltip");
    }
}
