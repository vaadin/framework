/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.slider;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
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

        assertSliderHandlePositionIs(38);

        // Move slider handle from 38px to 150px
        moveSlider(112);

        assertSliderHandlePositionIs(150);
        driver.findElement(By.id("disableButton")).click();

        assertSliderIsDisabled();
        assertSliderHandlePositionIs(150);
    }

    private void assertSliderIsDisabled() {
        WebElement slider = driver.findElement(By.className("v-slider"));
        assertThat(slider.getAttribute("class"), containsString("v-disabled"));
    }

    private void moveSlider(int offset) {
        WebElement element = vaadinElement("/VVerticalLayout[0]/Slot[0]/VSlider[0]/domChild[2]/domChild[0]");
        new Actions(driver).dragAndDropBy(element, offset, 0).perform();
        testBench().waitForVaadin();
    }

    private void assertSliderHandlePositionIs(int position) {
        WebElement handle = driver.findElement(By.className("v-slider-handle"));

        assertThat(handle.getCssValue("margin-left"), is(position + "px"));
    }
}