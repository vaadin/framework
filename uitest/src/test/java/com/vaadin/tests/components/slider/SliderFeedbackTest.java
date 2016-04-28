/*
 * Copyright 2000-2014 Vaadin Ltd.
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

        double value = Double.valueOf(findElement(
                By.className("v-slider-feedback")).getText());

        // Allow for some tolerance due to, you guessed it, IE8
        assertLessThan("Unexpected feedback value {1} < {0}", 505000000000.0,
                value);
        assertGreater("Unexpected feedback value {1} > {0}", 510000000000.0,
                value);
    }
}
