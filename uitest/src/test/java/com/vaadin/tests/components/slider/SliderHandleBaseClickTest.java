package com.vaadin.tests.components.slider;

import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class SliderHandleBaseClickTest extends MultiBrowserTest {
        @Test
        public void testHandlerHasMoved() {
                openTestURL();

                WebElement base = findElement(By.className("v-slider-base"));

                int offsetStep=base.getSize().getWidth()/10;
                new Actions(driver).dragAndDropBy(base, offsetStep, 0).perform();
                testBench().waitForVaadin();

                double valueBefore = Double.valueOf(
                        findElement(By.className("v-slider-feedback")).getText());

                new Actions(driver).dragAndDropBy(base, offsetStep*5, 0).perform();
                testBench().waitForVaadin();
                double valueAfter= Double.valueOf(
                        findElement(By.className("v-slider-feedback")).getText());
                assertGreater("Unexpected feedback value {1} > {0}", valueAfter,
                        valueBefore);
        }
}
