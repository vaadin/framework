package com.vaadin.tests.components.orderedlayout;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ErrorIndicatorTest extends MultiBrowserTest {

    @Test
    public void verifyTooltips() {
        String tooltipText;
        openTestURL();

        showTooltip($(TextFieldElement.class).first());
        tooltipText = driver.findElement(By.className("v-tooltip")).getText();
        assertEquals(tooltipText, "Vertical layout tooltip");

        showTooltip($(TextFieldElement.class).get(1));
        tooltipText = driver.findElement(By.className("v-tooltip")).getText();
        assertEquals(tooltipText, "Horizontal layout tooltip");
    }

    private void showTooltip(WebElement element) {
        Coordinates elementCoordinates = ((Locatable) element).getCoordinates();
        Mouse mouse = ((HasInputDevices) getDriver()).getMouse();
        mouse.mouseMove(elementCoordinates);
    }
}
