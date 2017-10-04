/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.themes.valo;

import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for disabled label opacity.
 *
 * @author Vaadin Ltd
 */
public class DisabledLabelTest extends MultiBrowserTest {

    @Test
    public void disabledLabelOpacity() {
        openTestURL();

        WebElement enabled = findElement(By.className("my-enabled"));
        String enabledOpacity = enabled.getCssValue("opacity");

        WebElement disabled = findElement(By.className("my-disabled"));
        String disabledOpacity = disabled.getCssValue("opacity");

        assertNotEquals(
                "Opacity value is the same for enabled and disabled label",
                enabledOpacity, disabledOpacity);
    }
}
