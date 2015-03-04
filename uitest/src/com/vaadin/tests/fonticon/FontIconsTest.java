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
package com.vaadin.tests.fonticon;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.Keys;

import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.newelements.ComboBoxElement;

public class FontIconsTest extends MultiBrowserTest {

    @Test
    public void checkScreenshot() throws IOException {
        openTestURL();
        compareScreen("all");
    }

    @Test
    public void comboBoxItemIconsOnKeyboardNavigation() throws Exception {
        openTestURL();

        ComboBoxElement comboBox = $(ComboBoxElement.class).first();

        // No initial value.
        assertEquals("", comboBox.getText());

        // Navigate to the first item with keyboard navigation.
        comboBox.sendKeys(400, Keys.ARROW_DOWN, Keys.ARROW_DOWN);

        // Value must be "One" without any extra characters.
        // See ticket #14660
        assertEquals("One", comboBox.getText());

        // Check also the second item.
        comboBox.sendKeys(Keys.ARROW_DOWN);
        assertEquals("Two", comboBox.getText());
    }
}
