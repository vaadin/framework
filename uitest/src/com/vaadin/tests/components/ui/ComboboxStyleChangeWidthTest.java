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
package com.vaadin.tests.components.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that changing a stylename will not cause the width parameter to be
 * removed from a combobox.
 * 
 * @author Vaadin Ltd
 */

public class ComboboxStyleChangeWidthTest extends MultiBrowserTest {

    @Test
    public void testWidthRetained() {
        openTestURL();

        WebElement comboBox = driver
                .findElement(By.className("v-filterselect"));
        String oldStyle = comboBox.getAttribute("style");

        WebElement button = driver.findElement(By.className("v-button"));
        button.click();
        String newStyle = comboBox.getAttribute("style");

        assertEquals("width has changed, should remain equal", oldStyle,
                newStyle);

    }

}
