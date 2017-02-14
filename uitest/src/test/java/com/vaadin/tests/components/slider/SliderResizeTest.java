/*
 * Copyright 2000-2017 Vaadin Ltd.
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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class SliderResizeTest extends MultiBrowserTest {

    @Test
    public void resizeSlider() throws IOException {
        openTestURL();

        // Verify the starting position.
        assertEquals("488px", getSliderHandlePosition());

        // Click on the button that reduces the layout width by 200px.
        driver.findElement(By.className("v-button")).click();

        // Assert that the slider handle was also moved.
        assertEquals("288px", getSliderHandlePosition());
    }

    private String getSliderHandlePosition() {
        WebElement handle = driver.findElement(By.className("v-slider-handle"));
        return handle.getCssValue("margin-left");
    }

}
