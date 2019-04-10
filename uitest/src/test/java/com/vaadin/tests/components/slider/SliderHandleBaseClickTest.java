package com.vaadin.tests.components.slider;

import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import static org.junit.Assert.assertEquals;

public class SliderHandleBaseClickTest extends MultiBrowserTest {
    private WebElement base;
    private int offsetStep;

    @Before
    public void setUp() throws Exception {
        super.setup();
        openTestURL();
        base = findElement(By.className("v-slider-base"));
        offsetStep = base.getSize().getWidth() / 10;
    }

    @Test
    public void testHandlerHasMoved() {
        // dragAndDropBy function starts calculating click position from the
        // middle of the component .
        // So the click will always be at the position (center + offsetStep)px

        // Will move by one from the middle
        new Actions(driver).dragAndDropBy(base, offsetStep, 0).perform();
        sleep(100);
        assertEquals("The Slider value should be 6 after moving by one offset",
                6, getSliderValue(), 0);
        // Will move by two from the middle, in this case from 5
        new Actions(driver).dragAndDropBy(base, offsetStep * 2, 0).perform();
        sleep(100);
        assertEquals("The Slider value should be 7 after moving by two offsets",
                7, getSliderValue(), 0);
    }

    private double getSliderValue() {
        return Double.valueOf(
                findElement(By.className("v-slider-feedback")).getText());
    }

    @Test
    public void testHandlerNotMoved() {
        // Disable click event handling
        findElement(By.id("toggleHandling")).click();
        new Actions(driver).dragAndDropBy(base, offsetStep, 0).perform();
        sleep(100);
        assertEquals(String.format(
                "Slider value should not have changed. Expected 3.0 , but was %f",
                getSliderValue()), 3.0,getSliderValue(), 0.0);
        // Enable click event handling
        findElement(By.id("toggleHandling")).click();
    }
}
