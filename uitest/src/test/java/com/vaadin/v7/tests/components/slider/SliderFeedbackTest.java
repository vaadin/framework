package com.vaadin.v7.tests.components.slider;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class SliderFeedbackTest extends MultiBrowserTest {

    @Test
    public void testValueGreaterThanMaxInt() {
        openTestURL();

        WebElement handle = findElement(By.className("v-slider-handle"));
        new Actions(driver).dragAndDropBy(handle, 400, 0).perform();
        testBench().waitForVaadin();

        double value = Double.valueOf(
                findElement(By.className("v-slider-feedback")).getText());

        // Allow for some tolerance due to, you guessed it, IE8
        assertLessThan("Unexpected feedback value {1} < {0}", 505000000000.0,
                value);
        assertGreater("Unexpected feedback value {1} > {0}", 510000000000.0,
                value);
    }
}
