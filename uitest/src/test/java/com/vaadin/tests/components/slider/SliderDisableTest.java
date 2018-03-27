package com.vaadin.tests.components.slider;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class SliderDisableTest extends MultiBrowserTest {
    @Test
    public void disableSlider() throws IOException {
        openTestURL();

        String originalPosition = getSliderHandlePosition();

        moveSlider(112);
        String expectedPosition = getSliderHandlePosition();
        assertThat(expectedPosition, is(not(originalPosition)));

        hitButton("disableButton");

        assertSliderIsDisabled();
        assertThat(getSliderHandlePosition(), is(expectedPosition));
    }

    private void assertSliderIsDisabled() {
        WebElement slider = driver.findElement(By.className("v-slider"));
        assertThat(slider.getAttribute("class"), containsString("v-disabled"));
    }

    private void moveSlider(int offset) {
        WebElement element = vaadinElement(
                "/VVerticalLayout[0]/Slot[0]/VSlider[0]/domChild[2]/domChild[0]");
        new Actions(driver).dragAndDropBy(element, offset, 0).perform();
        testBench().waitForVaadin();
    }

    private String getSliderHandlePosition() {
        WebElement handle = driver.findElement(By.className("v-slider-handle"));

        return handle.getCssValue("margin-left");
    }
}
